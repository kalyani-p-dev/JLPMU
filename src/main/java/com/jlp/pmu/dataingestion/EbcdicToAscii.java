package com.jlp.pmu.dataingestion;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
 
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
 
import com.jlp.pmu.utility.Utility;
 
@Service
public class EbcdicToAscii {
 
	private static final String INPUT_CHARSET_DEFAULT = "CP1047";
 
	private Charset input = charsetForName(INPUT_CHARSET_DEFAULT);
	private Charset output = Charset.defaultCharset();
	private Charset outputMFT = Charset.forName("windows-1252");
 
	private static final int INITIAL_BUFFER_SIZE = 2048;
	private static final int LF = '\n';
	private static final int NEL = 0x15;
	private static final int WS = ' ';
	static final Charset CP1047 = Charset.forName("Cp1047");
	private static final char[] NON_PRINTABLE_EBCDIC_CHARS = new char[] { 0x80 };
	private static final char[] NON_PRINTABLE_EBCDIC_CHARS_FOR_MFT = new char[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
			0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
			0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89,
			0x8a, 0x8b, 0x8c, 0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b,
			0x9c, 0x9d, 0x9e, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae,
			0xaf, 0xb0, 0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0,
			0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2,
			0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4,
			0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6,
			0xf7, 0xf8, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff, 0x7f };
	private int fixedLength = -1;
	private static File source;
	private File ascciFileAbsPath;
	public File destinationfile;
	private String report_type;
	Properties propObj;
	Utility utilObj;
	boolean specialChar = false;
 
	public File convertEbcdicToAscii(String absPathOfEbcdiFile) {
		
		System.out.println("absPathOfEbcdiFile=="+absPathOfEbcdiFile);
 
		File sourceFile = new File(absPathOfEbcdiFile);
		source = sourceFile;
		//new EbcdicToAscii().parseArguments().convert();
	//	new EbcdicToAscii().convert(absPathOfEbcdiFile);
		convert(absPathOfEbcdiFile);
		return destinationfile;
	}
 
	public EbcdicToAscii() {
		propObj = Utility.readPropertiesFile();
		utilObj = new Utility();
	}
 
	private EbcdicToAscii parseArguments() {
		if (source == null) {
			printError("Missing source.");
		}
		if (ascciFileAbsPath == null) {
			printError("Missing destination.");
		}
		return this;
	}
 
	private static void printUsage() {
		log("Convert source into destination.");
 
		System.exit(0);
	}
 
	private void convert(String absPathOfEbcdiFile) {
		try {
			String filePath = null;
			String reportHeader = null;
			String headerValueS;
 
			if (org.apache.commons.lang3.StringUtils.containsAnyIgnoreCase(absPathOfEbcdiFile, "mq")) {
				ascciFileAbsPath = new File(propObj.getProperty("absPathOfAscciFIleDirectory") + "mq");
				report_type = "MQ";
			} else {
				ascciFileAbsPath = new File(propObj.getProperty("absPathOfAscciFIleDirectory") + "mft");
				report_type = "MFT";
				output = outputMFT;
			}
 
			if (source.isFile() && !source.isHidden()) {
				filePath = "" + File.separator + source.getName();
				File destFile = new File(ascciFileAbsPath, filePath);
				destinationfile = destFile;
				if (report_type.equals("MFT")) {
					reportHeader = checkMFTReportType(source.getPath());
					headerValueS = propObj.getProperty("STRUCTURE_MFT_REPORT");
					fixedLength = checkFixedLength(headerValueS, reportHeader);
				}
				convert(source, destFile);
			} else {
				List<String> files = listFiles(source);
				for (String s : files) {
					File sourceFile = new File(source, s);
					File destFile = new File(ascciFileAbsPath, s);
					// log("Converting " + sourceFile + " into " + destFile);
					destFile.getParentFile().mkdirs();
					System.out.println("destFile::" + destFile.getAbsolutePath());
					if (report_type.equals("MFT")) {
						reportHeader = checkMFTReportType(sourceFile.getPath());
						headerValueS = propObj.getProperty("STRUCTURE_MFT_REPORT");
						fixedLength = checkFixedLength(headerValueS, reportHeader);
					}
					convert(sourceFile, destFile);
				}
			}
			log("SUCCESS");
			 //MessageProcessor ascciFileProcessor = new MessageProcessor();
			 //ascciFileProcessor.processInputFile(ascciFileAbsPath + filePath);
		} catch (Exception e) {
			log("Unable to convert files", e);
			log("FAILURE");
		}
	}
 
	private int checkFixedLength(String headerValueS, String reportHeader) {
		int fixedLengthValue;
		if (StringUtils.containsAnyIgnoreCase(headerValueS, reportHeader)) {
			fixedLengthValue = 256;
			//System.out.println("fixedLength:::::" + fixedLength);
		} else {
			fixedLengthValue = -1;
			//System.out.println("fixedLength:::::" + fixedLength);
		}
		return fixedLengthValue;
	}
 
	private String checkMFTReportType(String absPathOfEbcdiFile) {
		String dataString = "";
		String headerValue = "";
		try (FileInputStream ebcdicInputStream = new FileInputStream(absPathOfEbcdiFile);
				InputStreamReader ebcdicReader = new InputStreamReader(ebcdicInputStream, "IBM500");) {
			int data;
			while ((data = ebcdicReader.read()) != -1) {
				dataString += (char) data;
			}
			String[] items = dataString.split(" ");
			headerValue = StringUtils.substring(items[0], 0, 6);
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		return headerValue;
	}
 
	private static List<String> listFiles(File dir) {
		List<String> files = new ArrayList<String>();
		recursivelyListFiles(dir, "", files);
		return files;
	}
 
	private static void recursivelyListFiles(File dir, String relativePath, List<String> files) {
		for (String s : dir.list()) {
			String path = relativePath + File.separator + s;
			File file = new File(dir, s);
			if (file.isFile() && !file.isHidden()) {
				files.add(path);
			} else if (file.isDirectory() && !file.isHidden()) {
				recursivelyListFiles(file, path, files);
			}
		}
	}
 
	private static Charset charsetForName(String charsetName) {
		return Charset.forName(charsetName);
	}
 
	public void convert(File ebcdicInputFile, File convertedOutputFile) {
		Reader reader = null;
		Writer writer = null;
		Utility utilObj = new Utility();
		utilObj.createDirectory(convertedOutputFile.getParent());
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(ebcdicInputFile), input));
			int[] ebcdicInput = loadContent(reader);
			//System.out.println("ebcdicInput data::::" + ebcdicInput);
			close(reader);
			
			
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(convertedOutputFile), output));
			//System.out.println("writer data:" + writer);
			if (report_type.equals("MQ")) {
				convert(ebcdicInput, writer);
			} else {
				convertMFT(ebcdicInput, writer);
			}
		} catch (Exception e) {
		} finally {
			close(writer);
		}
	}
 
	private void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
		}
	}
 
	void convert(String input, Writer convertedOutputWriter) throws IOException {
		convert(loadContent(new StringReader(input)), convertedOutputWriter);
	}
 
	private void convert(int[] ebcdicInput, Writer convertedOutputWriter) throws IOException {
		int convertedChar;
		for (int index = 0; index < ebcdicInput.length; index++) {
			int character = ebcdicInput[index];
			if (fixedLength != -1 && index > 0 && index % fixedLength == 0) {
				convertedOutputWriter.append((char) LF);
			}
			if (fixedLength == -1 && character == NEL) {
				convertedChar = LF;
			} else {
				convertedChar = replaceNonPrintableCharacterByWhitespace(character);
				if (convertedChar == ' ') {
					convertedOutputWriter.append((char) convertedChar);
				} else {
					convertedOutputWriter.append((char) character);
				}
			}
		}
		//System.out.println("convertedOutputWriter data::" + convertedOutputWriter);
	}
 
	private int replaceNonPrintableCharacterByWhitespace(int character) {
		for (char nonPrintableChar : NON_PRINTABLE_EBCDIC_CHARS) {
			if (nonPrintableChar == (char) character) {
				return WS;
			}
		}
		return character;
	}
 
	private void convertMFT(int[] ebcdicInput, Writer convertedOutputWriter) throws IOException {
		int convertedChar;
		System.out.println("report_typexxxxxxxx:::::" + report_type);
		for (int index = 0; index < ebcdicInput.length; index++) {
			int character = ebcdicInput[index];
			if (fixedLength != -1 && index > 0 && index % fixedLength == 0) {
				convertedOutputWriter.append((char) LF);
			}
			if (fixedLength == -1 && character == NEL) {
				convertedChar = LF;
			} else {
				convertedChar = replaceNonPrintableCharacterByWhitespaceMFT(character);
				if (convertedChar == ' ' && specialChar) {
				} else if (!specialChar && convertedChar == ' ') {
					convertedOutputWriter.append((char) convertedChar);
				} else {
					convertedOutputWriter.append((char) character);
				}
				specialChar = false;
			}
		}
	}
 
	private int replaceNonPrintableCharacterByWhitespaceMFT(int character) {
		for (char nonPrintableChar : NON_PRINTABLE_EBCDIC_CHARS_FOR_MFT) {
			if (nonPrintableChar == (char) character) {
				specialChar = true;
				return WS;
			}
		}
		return character;
	}
 
	private int[] loadContent(Reader reader) throws IOException {
		int[] buffer = new int[INITIAL_BUFFER_SIZE];
		int bufferIndex = 0;
		int bufferSize = buffer.length;
		int character;
		while ((character = reader.read()) != -1) {
			if (bufferIndex == bufferSize) {
				buffer = resizeArray(buffer, bufferSize + INITIAL_BUFFER_SIZE);
				bufferSize = buffer.length;
			}
			buffer[bufferIndex++] = character;
		}
		return resizeArray(buffer, bufferIndex);
	}
 
	final int[] resizeArray(int[] orignalArray, int newSize) {
		int[] resizedArray = new int[newSize];
		for (int i = 0; i < newSize && i < orignalArray.length; i++) {
			resizedArray[i] = orignalArray[i];
		}
		return resizedArray;
	}
 
	private static void printError(String message) {
		log("");
		log(message);
		printUsage();
	}
 
	private static void log(String message) {
		System.out.println(message);
	}
 
	private static void log(String message, Throwable e) {
		System.out.println(message);
		e.printStackTrace();
	}
 
}