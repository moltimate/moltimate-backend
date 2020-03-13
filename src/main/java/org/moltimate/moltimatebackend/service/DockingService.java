package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;

import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.dto.request.ExportLigand;
import org.moltimate.moltimatebackend.dto.request.ExportRequest;
import org.moltimate.moltimatebackend.util.DockingUtils.InMemoryMultipartFile;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class DockingService {
	@Value( "${autodock.url}" )
	private String autoDockURL;
	@Value( "${openbabel.url}" )
	private String openBabelURL;

	public String dockLigand(DockingRequest request) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		if (request.getLigand() == null) {
			request.setLigand(LigandService.fetchLigand(request.getLigandID()));
		}
		if (request.getMacromolecule() == null) {
			request.setMacromolecule(fetchMacromolecule(request.getMacromoleculeID()));
		}

		RestTemplate template = new RestTemplate();

		// Begin by converting macromolecule and ligand to pdbqt format using OpenBabel.

		MultiValueMap<String, Object> openBabelParams = new LinkedMultiValueMap<>();
		openBabelParams.add("molecule_1", new ByteArrayResource( request.getMacromolecule().getBytes() ){
			@Override
			public String getFilename() {
				return request.getMacromolecule().getOriginalFilename();
			}
		});
		openBabelParams.add("molecule_2", new ByteArrayResource( request.getLigand().getBytes() ){
			@Override
			public String getFilename() {
				return request.getLigand().getOriginalFilename();
			}
		});

		HttpEntity<MultiValueMap<String, Object>> entityBabelPost = new HttpEntity<>(openBabelParams, headers);
		String babelHash = template.postForEntity( openBabelURL, entityBabelPost, String.class ).getBody();

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

			String macroName = Objects.requireNonNull(request.getMacromolecule().getOriginalFilename())
					.substring( 0, request.getMacromolecule().getOriginalFilename().indexOf('.') ) + ".pdbqt";
			String ligandName = Objects.requireNonNull(request.getLigand().getOriginalFilename())
					.substring( 0, request.getLigand().getOriginalFilename().indexOf('.') ) + ".pdbqt";

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
					// MockMultipartFile is normally used for testing, but is useful in this case as it allows the
					// file to exist in memory.

					if( entry.getName().equalsIgnoreCase( "molecule_1" ) ) {
						request.setMacromolecule( new InMemoryMultipartFile( macroName, file ) );
					} else if( entry.getName().equalsIgnoreCase( "molecule_2" ) ) {
						request.setLigand( new InMemoryMultipartFile( ligandName, file ) );
					}
				}
			}
		} catch( HttpServerErrorException ex ) {
			throw new DockingJobFailedException("An error occurred when converting to pdbqt format",
					new InMemoryMultipartFile("job.zip", ex.getResponseBodyAsByteArray()));
		}

		// Macromolecule and ligand should both be converted to pdbqt format at this point.

		// Send POST request to AutoDock
		HttpEntity<MultiValueMap<String, Object>> entityAutoDockPost = new HttpEntity<>(request.toMap(), headers);
		ResponseEntity<String> responseAutoDockPost =
				template.postForEntity( autoDockURL, entityAutoDockPost, String.class );


		return responseAutoDockPost.getBody();
	}


	public Resource exportLigands(ExportRequest request) {
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

	public MultipartFile getDockingResult( String storage_hash ) {
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<byte[]> dockingResult =
					template.getForEntity(autoDockURL + "?jobId=" + storage_hash, byte[].class);

			if (new String(Objects.requireNonNull(dockingResult.getBody())).equalsIgnoreCase("Job still processing.")) {
				throw new JobProcessingExeption(String.format("Job %s not completed yet", storage_hash));
			} else {
				return new InMemoryMultipartFile("job.zip", dockingResult.getBody());
			}
		} catch( HttpServerErrorException ex ) {
			throw new DockingJobFailedException(String.format("Job %s was not completed successfully", storage_hash),
					new InMemoryMultipartFile("job.zip", ex.getResponseBodyAsByteArray()));
		}
	}

	public static MultipartFile fetchMacromolecule(String pdbID) {
		if (pdbID == null) {
            throw new InvalidFileException("Unable to fetch remote Macromolecule File: no pdbID provided");
		}
		
		log.info("Fetching Macromolecule {} for Docking Request", pdbID);
		
		try {
			return new InMemoryMultipartFile(
				pdbID+".pdb", 
				StructureIO.getStructure(pdbID).toPDB().getBytes()
			);
		} catch (StructureException e) {
			throw new InvalidFileException("PDB ID does not correspond to a known structure");
		} catch (IOException e) {
			throw new InvalidFileException("Unable to fetch remote Macromolecule");
		}
	}
}
