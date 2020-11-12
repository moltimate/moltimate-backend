package org.moltimate.moltimatebackend.util;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.moltimate.moltimatebackend.dto.response.PdbQueryResponse;
import org.moltimate.moltimatebackend.exception.InvalidPdbIdException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DockingUtils {
	public static String SDF_URL = "https://files.rcsb.org/ligands/download/%s_ideal.sdf";
    public static class InMemoryMultipartFile implements MultipartFile {
		private String originalFilename;
		private byte[] bytes;

		public InMemoryMultipartFile( String name, byte[] bytes ) {
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