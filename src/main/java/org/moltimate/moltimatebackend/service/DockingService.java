package org.moltimate.moltimatebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.moltimate.moltimatebackend.dto.request.DockingRequest;
import org.moltimate.moltimatebackend.exception.DockingJobFailedException;
import org.moltimate.moltimatebackend.exception.JobProcessingExeption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

		RestTemplate template = new RestTemplate();

		// Begin by converting macromolecule and ligand to pdbqt format using OpenBabel.

		MultiValueMap<String, Object> openBabelParams = new LinkedMultiValueMap<>();
		openBabelParams.add("molecule 1", new ByteArrayResource( request.getMacromolecule().getBytes() ){
			@Override
			public String getFilename() {
				return request.getMacromolecule().getOriginalFilename();
			}
		});
		openBabelParams.add("molecule 2", new ByteArrayResource( request.getLigand().getBytes() ){
			@Override
			public String getFilename() {
				return request.getLigand().getOriginalFilename();
			}
		});

		HttpEntity<MultiValueMap<String, Object>> entityBabelPost = new HttpEntity<>(openBabelParams, headers);
		String babelHash = template.postForEntity( openBabelURL, entityBabelPost, String.class ).getBody();

		// Check the status of the file conversion every 5 seconds.

		try {
			ResponseEntity<byte[]> babelConversion =
					template.getForEntity( openBabelURL + "?storage_hash=" + babelHash, byte[].class );
			while( babelConversion.getStatusCode().equals(HttpStatus.NON_AUTHORITATIVE_INFORMATION) ) {
				try {
					Thread.sleep( 5000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				babelConversion = template.getForEntity( openBabelURL + "?storage_hash=" + babelHash, byte[].class );
			}

			// Status is now completed.
			byte[] babelZip = babelConversion.getBody();
			List<MultipartFile> unzipped = new ArrayList<>();
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
					MultipartFile unzippedFile = new InMemoryMultipartFile(entry.getName(), file);
					unzipped.add( unzippedFile );
				}

				String macroName = request.getMacromolecule().getName();
				String ligandName = request.getLigand().getName();

				// Replace macromolecule and ligand files with converted version.
				for( MultipartFile file: unzipped ) {
					if( file.getName().equalsIgnoreCase( macroName ) ) {
						request.setMacromolecule( file );
					} else if( file.getName().equalsIgnoreCase( ligandName ) ) {
						request.setLigand( file );
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

	public MultipartFile getDockingResult( String storage_hash ) {
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<byte[]> dockingResult =
					template.getForEntity(autoDockURL + "?storage_hash=" + storage_hash, byte[].class);

			if (dockingResult.getStatusCode().equals(HttpStatus.OK)) {
				return new InMemoryMultipartFile("job.zip", dockingResult.getBody());
			} else {
				throw new JobProcessingExeption(String.format("Job %s not completed yet", storage_hash));
			}
		} catch( HttpServerErrorException ex ) {
			throw new DockingJobFailedException(String.format("Job %s was not completed successfully", storage_hash),
					new InMemoryMultipartFile("job.zip", ex.getResponseBodyAsByteArray()));
		}
	}

	private static class InMemoryMultipartFile implements MultipartFile {
		private String originalFilename;
		private byte[] bytes;

		InMemoryMultipartFile( String name, byte[] bytes ) {
			this.originalFilename = name;
			this.bytes = bytes;
		}

		@Override
		public String getName() {
			return originalFilename == null ? null : originalFilename.substring( 0, originalFilename.indexOf('.') );
		}

		@Override
		public String getOriginalFilename() {
			return originalFilename;
		}

		@Override
		public String getContentType() {
			return MediaType.MULTIPART_FORM_DATA_VALUE;
		}

		@Override
		public boolean isEmpty() {
			return bytes == null || bytes.length == 0;
		}

		@Override
		public long getSize() {
			return bytes == null ? 0 : bytes.length;
		}

		@Override
		public byte[] getBytes() throws IOException {
			return bytes;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream( bytes );
		}

		@Override
		public void transferTo(File file) throws IOException, IllegalStateException {
			FileOutputStream fos = new FileOutputStream( file );
			fos.write( bytes );
		}
	}
}
