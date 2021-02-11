package org.moltimate.moltimatebackend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

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

	/**
	 * Replace atoms in a protein (or ligand) file
	 */
	public static byte[] replaceAtoms(byte[] byteFile) throws IOException {
		File sourcefile = new File("tmp");
		FileUtils.writeByteArrayToFile(sourcefile, byteFile);
    	String replaceTablePath = "";

    	// NOTE: It is critical that named groups only exist for the atoms that we expect to replace
		// Do not create named groups for anything other than the atoms subject to replacement.
    	String pdbRegex =
			"((ATOM)*(HETATM)*)\\s+" +
			"((\\d+))\\s+" +
			"(?<atom>(\\w+))\\s*" +
			"(\\w{3})\\s+A\\s*\\d+\\s+" +
			"((\\-?\\d+\\.\\d+))\\s+((\\-?\\d+\\.\\d+))\\s+((\\-?\\d+\\.\\d+))\\s+" +
			"(\\d+\\.\\d+)\\s+((\\-?\\d+\\.\\d+))\\s+" +
			"(?<atom2>(\\w+))\\s*";

    	String cifRegex =
			"(ATOM)\\s+\\d+\\s+" +
			"(?<atom>\\w+)\\s*" +
			"(?<atom2>\\w+)\\s+" +
			"\\.\\s+\\w+\\s+\\w\\s+" +
			"\\d+\\s+\\d+\\s+\\?\\s+" +
			"\\-?\\d+\\.\\d+\\s+" +
			"\\-?\\d+\\.\\d+\\s+" +
			"\\-?\\d+\\.\\d+\\s+" +
			"\\d+\\.\\d+\\s+" +
			"\\-?\\d+\\.\\d+\\s+\\?\\s+\\d+\\s+\\w+\\s+\\w+\\s+" +
			"(?<atom3>\\w+)\\s+\\d+\\s*";

    	String sdfRegex =
			"(\\-?\\d+\\.\\d+)\\s+" +
			"(\\-?\\d+\\.\\d+)\\s+" +
			"(\\-?\\d+\\.\\d+)\\s+" +
			"(?<atom>\\w+)\\s+" +
			"(\\-?\\d+\\.?\\d*)\\s+" +
			"(\\-?\\d+\\.?\\d*)\\s+" +
			"(\\-?\\d+\\.?\\d*)\\s+" +
			"(\\-?\\d+\\.?\\d*)\\s+" +
			"(\\-?\\d+\\.?\\d*)";

    	// Select the regular expression that will be used to match lines based on the file's extension.
		String workingRegex;
		String filename = sourcefile.getName().toLowerCase();
    	if( filename.endsWith(".pdb") ){
			workingRegex = pdbRegex;
		} else if(filename.endsWith(".cif")) {
			workingRegex = cifRegex;
		} else if(filename.endsWith(".sdf")){
    		workingRegex = sdfRegex;
		} else {
    		throw new IllegalArgumentException("\'sourcefile\' parameter name does not contain a known filename");
		}
		Pattern linePattern = Pattern.compile(workingRegex);
    	Matcher m;

		// Create a temporary file in the current directory
		File tempFile = File.createTempFile( "tmp", null, sourcefile.getAbsoluteFile().getParentFile() );
		tempFile.createNewFile();

		// TODO: Build this from a configuration file
		Map<String, String> replaceTable = new HashMap<String, String>();
		replaceTable.put("SE", "S");

		// Open the protein file and replace atoms which have known replacements
		BufferedReader reader = null;
		BufferedWriter writer = null;
    	try {
			reader = new BufferedReader(new FileReader(sourcefile.getPath()));
			writer = new BufferedWriter(new FileWriter(tempFile.getPath()));
			String line = reader.readLine();
			while( line != null ){

				m = linePattern.matcher(line);
				if(m.find()){
					// There exists a replacement for this atom
					if( replaceTable.containsKey( m.group("atom") )){
						writer.write( m.replaceAll( replaceTable.get( m.group("atom") ) ));
					}
				} else {
					writer.write(line);
				}
				line = reader.readLine();
			}
		} catch(IOException exp){
    		// File not found, or cannot be read from
    		throw exp;
		} finally {
    		if( reader != null ){ reader.close(); }
    		if( writer != null ){ writer.close(); }
		}

		return FileUtils.readFileToByteArray( tempFile );
	}
}