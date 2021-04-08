package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.biojava.nbio.structure.*;
import org.moltimate.moltimatebackend.dto.alignment.SuccessfulAlignment;
import org.moltimate.moltimatebackend.dto.request.AlignmentRequest;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.dto.request.ExportLigand;
import org.moltimate.moltimatebackend.dto.request.ExportRequest;
import org.moltimate.moltimatebackend.dto.response.ActiveSiteFilters;
import org.moltimate.moltimatebackend.dto.response.FreeEnergyResponse;
import org.moltimate.moltimatebackend.dto.response.QueryAlignmentResponse;
import org.moltimate.moltimatebackend.model.ActiveSite;
import org.moltimate.moltimatebackend.model.Residue;
import org.moltimate.moltimatebackend.util.ActiveSiteUtils;
import org.moltimate.moltimatebackend.util.DockingUtils;
import org.moltimate.moltimatebackend.util.DockingUtils.InMemoryMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.moltimate.moltimatebackend.exception.DockingJobFailedException;
import org.moltimate.moltimatebackend.exception.InvalidFileException;
import org.moltimate.moltimatebackend.exception.JobProcessingExeption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class DockingService {
	@Value( "${autodock.url}" )
	private String autoDockURL;
	@Value( "${openbabel.url}" )
	private String openBabelURL;

	@Autowired
	private AlignmentService alignmentService;

	private final String TOPDBCONVERSION = "toPDB";
	private final String TOPDBQTCONVERSION = "toPDBQT";

	public String dockLigand(DockingRequest request) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		if (request.getLigand() == null || request.getLigand().isEmpty()) {
			request.setLigand(LigandService.fetchLigand(request.getLigandID()));
		}
		if (request.getMacromolecule() == null || request.getMacromolecule().isEmpty()) {
			request.setMacromolecule(fetchMacromolecule(request.getMacromoleculeID()));
		}

		// Begin by converting macromolecule and ligand to pdbqt format using OpenBabel.
		request.setMacromolecule( convertToPDBQT( request.getMacromolecule(), false ) );

		request.setLigand( convertToPDBQT( request.getLigand(), true ) );

		// Macromolecule and ligand should both be converted to pdbqt format at this point.

		// Send POST request to AutoDock
		RestTemplate template = new RestTemplate();
		HttpEntity<MultiValueMap<String, Object>> entityAutoDockPost = new HttpEntity<>(request.toMap(), headers);
		ResponseEntity<String> responseAutoDockPost =
				template.postForEntity( autoDockURL, entityAutoDockPost, String.class );


		return responseAutoDockPost.getBody();
	}

	private MultipartFile convertToPDBQT( MultipartFile molecule, boolean ligand ) throws IOException {
		if( molecule == null ) {
			throw new InvalidFileException("No ligand or macromolecule file supplied and no PDB ID supplied");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		RestTemplate template = new RestTemplate();

		MultiValueMap<String, Object> openBabelParams = new LinkedMultiValueMap<>();
		openBabelParams.add("molecule_1", new ByteArrayResource( molecule.getBytes() ){
			@Override
			public String getFilename() {
				return molecule.getOriginalFilename();
			}
		});

		if( ligand ) {
			openBabelParams.add("options", "-c");
		} else {
			openBabelParams.add("options", "-xr -c");
		}

		HttpEntity<MultiValueMap<String, Object>> entityBabelPost = new HttpEntity<>(openBabelParams, headers);
		String babelHash = template.postForEntity( openBabelURL + TOPDBQTCONVERSION, entityBabelPost, String.class ).getBody();

		// Check the status of the file conversion every 20 seconds.

		try {
			ResponseEntity<byte[]> babelConversion =
					template.getForEntity( openBabelURL + "?storage_hash=" + babelHash, byte[].class );
			while( babelConversion.getStatusCode().equals(HttpStatus.MULTIPLE_CHOICES) ) {
				try {
					Thread.sleep( 20000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				babelConversion = template.getForEntity( openBabelURL + "?storage_hash=" + babelHash, byte[].class );
			}

			String moleculeName = Objects.requireNonNull(molecule.getOriginalFilename())
					.substring( 0, molecule.getOriginalFilename().indexOf('.') ) + ".pdbqt";

			// Status is now completed.
			byte[] babelZip = babelConversion.getBody();
			if (babelZip != null) {
				// Read zipped contents into memory
				InputStream is = new ByteArrayInputStream(babelZip);
				ZipInputStream zipInputStream = new ZipInputStream(is);
				ZipEntry entry;
				while((entry = zipInputStream.getNextEntry()) != null) {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					int n;
					while((n = zipInputStream.read(buf, 0, 1024)) != -1) {
						output.write(buf, 0, n);
					}
					byte[] file = output.toByteArray();

					if( entry.getName().contains( ".pdbqt" ) ) {
						return new InMemoryMultipartFile( moleculeName, file );
					}
				}
			}
		} catch( HttpServerErrorException ex ) {
			throw new DockingJobFailedException("An error occurred when converting to pdbqt format",
					new InMemoryMultipartFile("job.zip", ex.getResponseBodyAsByteArray()));
		}

		return null;
	}


	public Resource exportLigands(ExportRequest request) {

		try{
			//Add babelResult to ZIP
			MultipartFile babelResult = getBabelResult(request.getBabelJobId());
			InputStream is = babelResult.getInputStream();
			ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
			ZipOutputStream zipOS = new ZipOutputStream(byteArrayOS);
			ZipEntry zipEntry = new ZipEntry(babelResult.getOriginalFilename());
			zipOS.putNextEntry(zipEntry);

			String content = new String(babelResult.getBytes());
			String[] lines = content.split("\n");
			ArrayList<String> updatedLines = new ArrayList<String>();

			Boolean deleting = false;
			int numberConfigs = request.getSelectedConfigs().size();
			int currentConfig = 0;
			for(int i = 0; i < lines.length; i++){
				if(i != 0) {
					if (lines[i].contains("MODEL") && !deleting) {
						if (!request.getSelectedConfigs().get(currentConfig)) {
							deleting = true;
						}
					}
					if(!deleting){
						updatedLines.add(lines[i]);
					}
					if(lines[i].contains("ENDMDL")){
						currentConfig++;
					}
				}
			}

			byte[] bytes = new byte[1024];
			int length;
			for(String line : updatedLines){
				line = line.toUpperCase();
				line = line.concat("\r");
				zipOS.write(line.getBytes(), 0, line.getBytes().length);
			}

			//Add docking info csv file to ZIP
			ByteArrayResource csv = createCSV(request);
			is = csv.getInputStream();
			zipEntry = new ZipEntry("ligands.csv");
			zipOS.putNextEntry(zipEntry);

			bytes = new byte[1024];
			while((length = is.read(bytes)) >= 0){
				zipOS.write(bytes, 0, length);
			}
			zipOS.close();
			return new ByteArrayResource(byteArrayOS.toByteArray());

		}catch (IOException e){
			log.info("Failed to fetch pbd file for download : jobId{" + request.getBabelJobId() + "}");
		}
		return null;
	}


	private ByteArrayResource createCSV(ExportRequest request){
		StringBuilder csvOutput = new StringBuilder();
		csvOutput.append("Name,Mode Number,Binding Energy,RMSD Lower,RMSD Upper\n");
		for(ExportLigand ligand : request.getLigands()) {
			csvOutput.append("\"");
			csvOutput.append(ligand.getName());
			csvOutput.append("\"");
			csvOutput.append(",");
			csvOutput.append(ligand.getModeNumber());
			csvOutput.append(",");
			csvOutput.append(ligand.getBindingEnergy());
			csvOutput.append(",");
			csvOutput.append(ligand.getRmsdLower());
			csvOutput.append(",");
			csvOutput.append(ligand.getRmsdUpper());
			csvOutput.append("\n");
		}

		return new ByteArrayResource( csvOutput.toString().getBytes() );
	}

	public FreeEnergyResponse getDockingResult( String storage_hash, String pdbId ) throws IOException {
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<byte[]> dockingResult =
					template.getForEntity(autoDockURL + "?jobId=" + storage_hash, byte[].class);

			if (new String(Objects.requireNonNull(dockingResult.getBody())).equalsIgnoreCase("Job still processing.")) {
				throw new JobProcessingExeption(String.format("Job %s not completed yet", storage_hash));
			} else {
				byte[] jobZip = dockingResult.getBody();

				if( jobZip != null ) {
					InputStream is = new ByteArrayInputStream(jobZip);
					ZipInputStream zipInputStream = new ZipInputStream(is);
					ZipEntry entry;
					byte[] logFile = null;
					byte[] ligandFile = null;
					byte[] proteinFile = fetchMacromolecule(pdbId).getBytes();
//					byte[] proteinFileTemp = DockingUtils.replaceAtoms( proteinFile );
					while((entry = zipInputStream.getNextEntry()) != null) {
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int n;
						while ((n = zipInputStream.read(buf, 0, 1024)) != -1) {
							output.write(buf, 0, n);
						}
						byte[] file = output.toByteArray();

						if( entry.getName().equalsIgnoreCase("log.txt") ) {
							logFile = file;
						} else if( entry.getName().contains(".pdbqt") ) {
							ligandFile = file;
						}
					}

					if( ligandFile != null ) {
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.MULTIPART_FORM_DATA);

						MultiValueMap<String, Object> openBabelParams = new LinkedMultiValueMap<>();
						openBabelParams.add("molecule_1", new ByteArrayResource(proteinFile) {
							@Override
							public String getFilename() {
								return pdbId + ".pdb";
							}
						});
						openBabelParams.add("molecule_2", new ByteArrayResource(ligandFile) {
							@Override
							public String getFilename() {
								return "Ligand.pdbqt";
							}
						});
						openBabelParams.add("options", "-c");

						HttpEntity<MultiValueMap<String, Object>> entityBabelPost = new HttpEntity<>(openBabelParams, headers);
						String babelHash = template.postForEntity(openBabelURL + TOPDBCONVERSION, entityBabelPost, String.class).getBody();

						List<ActiveSiteFilters> activeSites = generateActiveSites( pdbId );

						return new FreeEnergyResponse( babelHash, FreeEnergyResponse.parseLog( logFile ), activeSites );
					}

					throw new DockingJobFailedException(String.format("Job %s was not completed successfully", storage_hash),
							new InMemoryMultipartFile("job.zip", dockingResult.getBody()));
				}
			}
		} catch( HttpServerErrorException ex ) {
			throw new DockingJobFailedException(String.format("Job %s was not completed successfully", storage_hash),
					new InMemoryMultipartFile("job.zip", ex.getResponseBodyAsByteArray()));
		}
		return null;
	}

	public MultipartFile getBabelResult( String storage_hash ) throws IOException {
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<byte[]> babelConversion =
					template.getForEntity(openBabelURL + "?storage_hash=" + storage_hash, byte[].class);
			if (babelConversion.getStatusCode().equals(HttpStatus.MULTIPLE_CHOICES)) {
				throw new JobProcessingExeption(String.format("Job %s not completed yet", storage_hash));
			} else if (babelConversion.getStatusCode().equals(HttpStatus.OK)) {
				byte[] babelZip = babelConversion.getBody();
				if (babelZip != null) {
					// Read zipped contents into memory
					InputStream is = new ByteArrayInputStream(babelZip);
					ZipInputStream zipInputStream = new ZipInputStream(is);
					ZipEntry entry;
					while ((entry = zipInputStream.getNextEntry()) != null) {
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int n;
						while ((n = zipInputStream.read(buf, 0, 1024)) != -1) {
							output.write(buf, 0, n);
						}
						byte[] file = output.toByteArray();

						if (entry.getName().contains("result.pdb")) {
							return new InMemoryMultipartFile("conversion.pdb", file);
						}
					}
				}
			} else {
				throw new DockingJobFailedException(String.format("Job %s was not completed successfully", storage_hash),
						new InMemoryMultipartFile("job.zip", babelConversion.getBody()));
			}
		} catch( HttpServerErrorException ex ) {
			throw new DockingJobFailedException(String.format("Job %s was not completed successfully", storage_hash),
					new InMemoryMultipartFile("job.zip", ex.getResponseBodyAsByteArray()));
		}
		return null;
	}

	public static MultipartFile fetchMacromolecule(String pdbID) {
		if (pdbID == null) {
            throw new InvalidFileException("Unable to fetch remote Macromolecule File: no pdbID provided");
		}

		log.info("Fetching Macromolecule {} for Docking Request", pdbID);

		RestTemplate template = new RestTemplate();

		ResponseEntity<byte[]> file = template.getForEntity("https://files.rcsb.org/download/"+ pdbID + ".pdb", byte[].class);
		if( file.getStatusCode().equals(HttpStatus.NOT_FOUND) ) {
			throw new InvalidFileException("PDB ID does not correspond to a known structure");
		} else if( file.getStatusCode().equals(HttpStatus.OK) ) {
			return new InMemoryMultipartFile(
					pdbID + ".pdb",
					file.getBody()
			);
		} else {
			throw new InvalidFileException(String.format("An error occurred when fetching file from pdb. Error is %s", file.getStatusCode().getReasonPhrase()));
		}
	}

	public List<ActiveSiteFilters> generateActiveSites( String pdbId ) throws IOException {
		if( pdbId == null || pdbId.isEmpty() ) {
			return Collections.emptyList();
		}

		List<ActiveSiteFilters> activeSites = new ArrayList<>();
		List<ActiveSite> activeSitesList = ActiveSiteUtils.getActiveSites();
		activeSitesList.removeIf(activeSite -> !activeSite.getPdbId().equalsIgnoreCase(pdbId));

		// If we don't have the protein directly, try to perform an alignment and use the lowest rmsd alignment instead
		if( activeSitesList.isEmpty() ) {
			QueryAlignmentResponse align = alignmentService.alignActiveSites(
					new AlignmentRequest(
							Collections.singletonList(pdbId),
							new ArrayList<>(),
							new ArrayList<>(),
							new ArrayList<>(),
							new ArrayList<>(),
							null,
							1.0
					)
			);
			if (
					align.getEntries() != null &&
							!align.getEntries().isEmpty() &&
							align.getEntries().get(0).getAlignments() != null &&
							!align.getEntries().get(0).getAlignments().isEmpty())
			{
				SuccessfulAlignment bestAlignment = align.getEntries().get(0).getAlignments().get(0);
				for (SuccessfulAlignment alignment : align.getEntries().get(0).getAlignments()) {
					if (alignment.getRmsd() < bestAlignment.getRmsd()) {
						bestAlignment = alignment;
					}
				}
				activeSitesList.add(new ActiveSite(bestAlignment.getPdbId(), bestAlignment.getActiveSiteResidues()));
			}
		}

		try {
			Structure struct = StructureIO.getStructure(pdbId);

			for( ActiveSite site: activeSitesList ) {
				for( Residue res: site.getResidues() ) {
					for( Chain chain: struct.getChain( res.getResidueChainName() ).getEntityInfo().getChains() ) {
						activeSites.add( new ActiveSiteFilters( Integer.parseInt( res.getResidueId() ), chain.getId(), res.getResidueAltLoc() == null ? "" : res.getResidueAltLoc() ) );
					}
				}
			}
		} catch (StructureException e) {
			e.printStackTrace();
		}

		return activeSites;
	}
}
