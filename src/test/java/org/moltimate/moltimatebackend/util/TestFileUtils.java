package org.moltimate.moltimatebackend.util;

import org.apache.commons.io.IOUtils;
import org.biojava.nbio.structure.Structure;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.moltimate.moltimatebackend.dto.MotifFile;
import org.moltimate.moltimatebackend.dto.request.MakeMotifRequest;
import org.moltimate.moltimatebackend.exception.InvalidFileException;
import org.moltimate.moltimatebackend.exception.MotifFileParseException;
import org.moltimate.moltimatebackend.model.Residue;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class TestFileUtils {

	@Test
	public void testCreateMotifFileNoStructureFile() {
		MakeMotifRequest request = new MakeMotifRequest();
		request.setPdbId( "1a0j" );
		request.setActiveSiteResidues( Arrays.asList(
				new Residue( "Asp", "A", "7", "" ).getIdentifier(),
				new Residue( "Glu", "B", "70", "" ).getIdentifier()
		) );
		request.setEcNumber( "3.4.21.4" );

		ResponseEntity<Resource> response = FileUtils.createMotifFile( request );
		Assert.assertNotNull( response );
		Assert.assertNotNull( response.getBody() );
		Assert.assertEquals( 200, response.getStatusCodeValue() );
		Assert.assertEquals( "1a0j.motif", response.getHeaders().getContentDisposition().getFilename() );
	}

	@Test
	public void testCreateMotifFileWithStructureFile() {
		MakeMotifRequest request = new MakeMotifRequest();
		request.setPdbId( "1a0j" );
		request.setActiveSiteResidues( Collections.emptyList() );
		request.setEcNumber( "3.4.21.4" );
		request.setStructureFile( createFile( "src/test/java/org/moltimate/moltimatebackend/util/1aoj.pdb" ) );

		ResponseEntity<Resource> response = FileUtils.createMotifFile( request );
		Assert.assertNotNull( response );
		Assert.assertNotNull( response.getBody() );
		Assert.assertEquals( 200, response.getStatusCodeValue() );
		Assert.assertEquals( "1a0j.motif", response.getHeaders().getContentDisposition().getFilename() );
	}

	@Test( expected = MotifFileParseException.class )
	public void testReadMotifFileParseException() {
		FileUtils.readMotifFile( createFile( "src/test/java/org/moltimate/moltimatebackend/util/1aoj.pdb" ) );
	}

	@Test
	public void testReadMotifFile() {
		MotifFile file = FileUtils.readMotifFile( createFile( "src/test/java/org/moltimate/moltimatebackend/util/1aoj.motif" ) );
		Assert.assertNotNull( file );
	}

	@Test( expected = InvalidFileException.class)
	public void testReadMotifFileMotifInvalidFile() throws IOException {
		MultipartFile mockFile = Mockito.mock( MultipartFile.class );
		Mockito.when( mockFile.getBytes() ).thenThrow( InvalidFileException.class );
		FileUtils.readMotifFile( mockFile );
	}

	@Test( expected = InvalidFileException.class)
	public void testGetStructureFromFileInvalidFile() throws IOException {
		MultipartFile mockFile = Mockito.mock( MultipartFile.class );
		Mockito.when( mockFile.getInputStream() ).thenThrow( IOException.class );
		FileUtils.getStructureFromFile( mockFile );
	}

	@Test
	public void testGetStructureFromFile() {
		Structure structure = FileUtils.getStructureFromFile( createFile( "src/test/java/org/moltimate/moltimatebackend/util/1aoj.motif" ) );
		Assert.assertNotNull( structure );
	}

	private MultipartFile createFile( String path ) {
		return new MultipartFile() {
			private File file = new File( path );

			@Override
			public String getName() {
				return file.getName();
			}

			@Override
			public String getOriginalFilename() {
				return file.getName();
			}

			@Override
			public String getContentType() {
				return "application/octet-stream";
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public long getSize() {
				try {
					return getBytes().length;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return 0;
			}

			@Override
			public byte[] getBytes() throws IOException {
				return IOUtils.toByteArray( file.toURI() );
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return new FileInputStream( file );
			}

			@Override
			public void transferTo(File file) throws IOException, IllegalStateException {

			}
		};
	}
}
