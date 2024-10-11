package com.jlp.pmu.utility;

import java.awt.image.BufferedImage;
// for file I/O
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.jlp.pmu.pojo.ReportMetaData;

public class Utility {

	public static Properties prop = null;
	public ReportMetaData repMetaData = null;
	List valuesListBeforeMasking;

	public Utility() {
// TODO Auto-generated constructor stub
	}
	
	
	
	public void populatePrintTransactionDetails(ReportMetaData repMetaDataObj) {
		repMetaDataObj.getTransactionVariableList().put("gen",
				StringUtils.substringBetween(repMetaDataObj.getAbsPathOfHtmlFile(), "-", "."));

		long branchIdLongValue, printerJobId;
		branchIdLongValue = Integer.parseInt(repMetaDataObj.getBranchId());
//printerJobId = Integer.parseInt(repMetaDataObj.getPrintJobId());

//int pageCount=StringUtils.indexOfAny(repMetaDataObj.getFinalHtmlContent(), "page-break");
//repMetaDataObj.getTransactionVariableList().put("pages", pageCount);
		int pageCount = StringUtils.indexOfAny(repMetaDataObj.getFinalHtmlContent(), "class=\"page-break-jlp");
		
		

		repMetaDataObj.getTransactionVariableList().put("printerName", repMetaDataObj.getPrinterName());
		
		if(StringUtils.isEmpty(repMetaDataObj.getBusinessFriendlyReportName()))
			repMetaDataObj.getTransactionVariableList().put("reportName", repMetaDataObj.getReportName());
		else
			repMetaDataObj.getTransactionVariableList().put("reportName", repMetaDataObj.getBusinessFriendlyReportName());
		
		
		System.out.println("After setting business friendly Name="+repMetaDataObj.getBusinessFriendlyReportName());
			
		repMetaDataObj.getTransactionVariableList().put("branchCode", branchIdLongValue);
		repMetaDataObj.getTransactionVariableList().put("startTime", repMetaDataObj.getStartTime());
		repMetaDataObj.getTransactionVariableList().put("endTime", repMetaDataObj.getEndTime());
		repMetaDataObj.getTransactionVariableList().put("pdfPath", repMetaDataObj.getPdfFileAbsPath());
		repMetaDataObj.getTransactionVariableList().put("pages", pageCount);
		repMetaDataObj.getTransactionVariableList().put("type", repMetaDataObj.getPrinterJobType());
		repMetaDataObj.getTransactionVariableList().put("status", repMetaDataObj.getJobStatus());
		
		
		
		
		
		
//repMetaDataObj.getTransactionVariableList().put("printerJob", repMetaDataObj.getPrintJobId());
	}


	synchronized public void htmlToPdf(String absPathOfPdfFile) {

		String htmlFolderPath = prop.getProperty("applicationDataRootDir") + "/output/"
				+ prop.getProperty("BranchName_" + repMetaData.getBranchId()) + "/"
				+ prop.getProperty("htmlOutputDirectory") + "/";

		String pfdfileAbsPath = absPathOfPdfFile, outFileName;
		String directoryPath = StringUtils.substringBeforeLast(pfdfileAbsPath, "/");
		createDirectory(directoryPath);

		outFileName = absPathOfPdfFile;

		String xhtml;

		String htmlContent = repMetaData.getFinalHtmlContent();

		/*
		 * to be re evaluated if(StringUtils.contains(htmlContent, '&')) { htmlContent =
		 * htmlContent.replaceAll("&", "&amp;"); }
		 * 
		 */
		try {
			Document document = Jsoup.parse(htmlContent, "UTF-8");
			document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
			xhtml = document.html();
			// String tmp= FileSystems.getDefault().;
//	 String baseUrl = FileSystems.getDefault()
//   		  .getPath("src/main/resources/")
//   		  .toUri().toURL().toString();
//	 

			String baseUrl = FileSystems.getDefault().getPath(htmlFolderPath).toUri().toURL().toString();

			OutputStream outputStream = new FileOutputStream(outFileName);

			ITextRenderer renderer = new ITextRenderer();

			renderer.setDocumentFromString(xhtml, baseUrl);
			SharedContext sharedContext = renderer.getSharedContext();
			sharedContext.setPrint(true);
			sharedContext.setInteractive(false);
			// renderer.setDocumentFromString(xhtml);
			renderer.layout();
			renderer.createPDF(outputStream);
			outputStream.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public List<String> listOfAllKeysContainsSubsString(String portionOfKey) {

		List<String> listOfAllKeysContainsGivenSubsString = new ArrayList<String>();

		Set<Object> set = prop.keySet();

		for (Object o : set) {
			String key = (String) o;
			if (StringUtils.containsAnyIgnoreCase(key, portionOfKey)) {
				listOfAllKeysContainsGivenSubsString.add(key);
			}
		}
		return listOfAllKeysContainsGivenSubsString;
	}

	public String htmlToXhtml(String html) {
		Document document = Jsoup.parse(html);
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		return document.html();
	}

	public String getUniqueFileNameBasedOnDateAndTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
		Date date = new Date();
		Random rand = new Random();
		// +"_"+String.valueOf(rand_dub1)
		String finalDynamicValue = formatter.format(date) + "-" + String.valueOf(rand.nextInt(10000000));
		return finalDynamicValue;
	}

	synchronized public void createBarCode(String barCodeValue) {
		System.out.println("Inside createBarCode");

		String htmlFolderPath = prop.getProperty("applicationDataRootDir") + "/output/"
				+ prop.getProperty("BranchName_" + repMetaData.getBranchId()) + "/"
				+ prop.getProperty("htmlOutputDirectory") + "/";

		createDirectory(htmlFolderPath);

		String barCodeFolderPath = prop.getProperty("BarCodeRootFolderAbsPath");

		try {
//String barcodeDir =propObj.getProperty("DestinationDirectoryOfBarcode");
			Code128Bean code128 = new Code128Bean();
			code128.setHeight(15f);
//code128.setModuleWidth(0.3);
			code128.setModuleWidth(0.5);
			code128.setQuietZone(2);
			code128.doQuietZone(true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 80,
					BufferedImage.TYPE_BYTE_BINARY, false, 0);
//code128.setMsgPosition(HumanReadablePlacement.HRP_NONE);
			code128.generateBarcode(canvas, barCodeValue);
			canvas.finish();
//write to png file
			FileOutputStream fos = new FileOutputStream(htmlFolderPath + barCodeValue + ".gif");
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();

			FileInputStream fis = new FileInputStream(barCodeFolderPath + barCodeValue + ".gif");
//FileOutputStream fosObj =  new FileOutputStream(prop.getProperty("applicationDataRootDir")+"\\output\\"+ prop.getProperty("BranchName_"+repMetaData.getBranchId())+"\\"+prop.getProperty("htmlOutputDirectory")+"\\"+barCodeValue+".gif");

			System.out.println("Gif file path in HTML folder " + prop.getProperty("applicationDataRootDir")
					+ "\\output\\" + prop.getProperty("BranchName_" + repMetaData.getBranchId()) + "\\"
					+ prop.getProperty("htmlOutputDirectory") + "\\" + barCodeValue + ".gif");
			/*
			 * int c;
			 * 
			 * // Condition check // Reading the input file till there is input // present
			 * while ((c = fis.read()) != -1) {
			 * 
			 * // Writing to output file of the specified // directory fosObj.write(c); }
			 * 
			 * if (fis != null) {
			 * 
			 * // Closing the fileInputStream fis.close(); } if (fosObj != null) {
			 * 
			 * // Closing the fileOutputStream fosObj.close();
			 * System.out.println("closed GIF file in the destination"); }
			 * 
			 * /* File src = new File(barCodeFolderPath+barCodeValue+".gif");
			 * 
			 * //barCodeAbsPathInHtml
			 * =prop.getProperty("applicationDataRootDir")+"\\output\\"+ prop.getProperty("
			 * BranchName_"+repMetaData.getBranchId())+"\\
			 * "+prop.getProperty("htmlOutputDirectory")+barCodeValue+".gif";
			 * 
			 * File dest = new File(prop.getProperty("applicationDataRootDir")
			 * +"\\output\\"+ prop.getProperty("BranchName_"+repMetaData.getBranchId())+"\\
			 * "+prop.getProperty("htmlOutputDirectory")+"\\"+barCodeValue+".gif");
			 * Files.copy(src.toPath(), dest.toPath());
			 */
		} catch (Exception e) {
// TODO: handle exception
		} finally {

		}

	}

	public List<String> getTheListOfAllWordsInLineWithSpaceAsDelimiter(String lineContent) {

		List listOfAllWordsInASentence = new ArrayList<String>();

		String str = lineContent;
		String[] arrWords = str.trim().split("\\s+");

//String[] arrWords = lineContent.split("[^a-zA-Z<>$]+");

		listOfAllWordsInASentence.addAll(Arrays.asList(arrWords));

		return listOfAllWordsInASentence;

	}

	public List<String> getTheListOfAllWordsInLine(String lineContent) {

		List listOfAllWordsInASentence = new ArrayList<String>();

		String[] arrWords = lineContent.split("[^a-zA-Z<>$10]+");

		listOfAllWordsInASentence.addAll(Arrays.asList(arrWords));

		return listOfAllWordsInASentence;

	}

	public String replaceSpecialCharacterWithEmptySpace(String stringValue, String exclusionCSV) {
		String stringWithoutSpecialCharacter = stringValue;

		String eachExclusionString = null, eachSpecialCharacter = null;
		List<String> listOfAllExclusionString = new ArrayList<String>();
		Iterator<String> exclusionListIter;

		exclusionCSV = exclusionCSV + ", ";

		if (!StringUtils.isEmpty(exclusionCSV)) {
			String[] exclusionList = StringUtils.split(exclusionCSV, ",");
			listOfAllExclusionString.addAll(Arrays.asList(exclusionList));
			exclusionListIter = listOfAllExclusionString.iterator();

			while (exclusionListIter.hasNext()) {

				eachSpecialCharacter = exclusionListIter.next();
				if (StringUtils.containsIgnoreCase(stringValue, eachSpecialCharacter)) {
					stringWithoutSpecialCharacter = StringUtils.replace(stringValue, eachSpecialCharacter, "");
				}
			}

		}
		return stringWithoutSpecialCharacter;
	}

	public boolean isLineContainsString(String sourceLine, String exclusionCSV) {
		boolean found = false;
		String eachExclusionString = null;
		List<String> listOfAllExclusionString = new ArrayList<String>();
		Iterator<String> exclusionListIter;

		if (!StringUtils.isEmpty(exclusionCSV)) {
			String[] exclusionList = StringUtils.split(exclusionCSV, ",");
			listOfAllExclusionString.addAll(Arrays.asList(exclusionList));
			exclusionListIter = listOfAllExclusionString.iterator();

			while (exclusionListIter.hasNext()) {
				if (StringUtils.containsIgnoreCase(sourceLine, exclusionListIter.next())) {
					found = true;
//System.out.println("string found in "+sourceLine);
					return found;
				}
			}

		}
		return found;
	}

	public boolean createDirectory(String directoryPath) {
		boolean directoryCreatedFlag = false;

		File directory = new File(String.valueOf(directoryPath));

		if (!directory.exists()) {
			try {
				Files.createDirectories(Paths.get(directoryPath));
				directoryCreatedFlag = true;
			} catch (IOException e) {
// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else
			directoryCreatedFlag = true;

		return directoryCreatedFlag;
	}

	public void createFile(String absPathOfNewFile, String fileContent) {
		String directoryPath = StringUtils.substringBeforeLast(absPathOfNewFile, "/");

		if (createDirectory(directoryPath)) {

			try {
				FileWriter fileWriter = new FileWriter(absPathOfNewFile);
				fileWriter.write(fileContent);
				fileWriter.close();

			} catch (IOException e) {
				System.out.println("An error occurred. while the creation of file");
				e.printStackTrace();
			}
		}

	}

	private void setTheIndexOfFirstPrintableCharacter(List<String> listOfAllLinesInFile) {

		Iterator<String> lineIter;
		String singleLineInEachRecord;
		if (listOfAllLinesInFile != null && listOfAllLinesInFile.size() > 0) {

			lineIter = listOfAllLinesInFile.iterator();

			while (lineIter.hasNext()) {
				singleLineInEachRecord = lineIter.next();
				if (StringUtils.contains(singleLineInEachRecord, "---------")) {
					repMetaData.setIndexOfFirstPrintableCharacter(
							StringUtils.indexOf(singleLineInEachRecord, "---------"));
					break;
				}

			}
		}

		return;
	}

	public String generateTabularReport(ReportMetaData repMetaDataObj) {
		String singleLineInEachRecord, allLinesWithinRecord, htmlLineAfterRemovingExtraValue;
		List<String> listOfEachRecord = repMetaDataObj.getListOfUniqueRecordsInSourceFile();
		List<String> listOfAllWordsInALine, listOfLinesInTheSourceFileToBePtinted,
				listOfEachLinesWithinAGivenRecord = new ArrayList<String>();
		Iterator<String> eachRecordIterator;
		Iterator<String> eachLineWithinRecordIterator;
		int counter = 0, rowCountWithinRecord = 0;
		boolean underLineFoundInTable = false;

		String finalEachLineHtmlContent = "", eachWordInInput = "";
		String[] arrOfAllStringInInputLine;
		List<String> htmlListForTabularStructure = new ArrayList<String>();
		boolean hashLineFounInSourceFile = false;
		List<String> styledArgumentList;
		int pageNumberCounter =1;

		setTheIndexOfFirstPrintableCharacter(repMetaDataObj.getListOfEachLinesInFile());

		if (listOfEachRecord != null && listOfEachRecord.size() > 0) {
			eachRecordIterator = listOfEachRecord.iterator();

			while (eachRecordIterator.hasNext()) {

				allLinesWithinRecord = eachRecordIterator.next();

//Arrays.asList(StringUtils.split(allLinesWithinRecord,"\n"));
				listOfEachLinesWithinAGivenRecord.clear();

				listOfEachLinesWithinAGivenRecord.addAll(Arrays.asList(StringUtils.split(allLinesWithinRecord, "\n")));

				rowCountWithinRecord = 0;

//setTheIndexOfFirstPrintableCharacter(listOfEachLinesWithinAGivenRecord);

				if (listOfEachLinesWithinAGivenRecord != null && listOfEachLinesWithinAGivenRecord.size() > 0) {
					eachLineWithinRecordIterator = listOfEachLinesWithinAGivenRecord.iterator();

					while (eachLineWithinRecordIterator.hasNext()) {
						hashLineFounInSourceFile = false;
						singleLineInEachRecord = eachLineWithinRecordIterator.next();

						if (StringUtils.containsIgnoreCase(singleLineInEachRecord,
								"--------------------------------------------"))
							hashLineFounInSourceFile = true;

						counter++;
						if (counter == 1)
							continue;

						rowCountWithinRecord++;

						arrOfAllStringInInputLine = StringUtils.split(singleLineInEachRecord, " ");

						if (repMetaDataObj.isTabularReport() && repMetaDataObj.getIndexOfFirstPrintableCharacter() > 0)
//if(repMetaDataObj.getIndexOfFirstPrintableCharacter()>0)

							htmlLineAfterRemovingExtraValue = StringUtils.substring(singleLineInEachRecord,
									repMetaDataObj.getIndexOfFirstPrintableCharacter());
//if(repMetaDataObj.isTabularReport())

						// htmlLineAfterRemovingExtraValue =
						// StringUtils.substringAfter(singleLineInEachRecord,
						// arrOfAllStringInInputLine[0]);
						else
							htmlLineAfterRemovingExtraValue = singleLineInEachRecord;

						if (StringUtils
								.isNoneEmpty(prop.getProperty(repMetaDataObj.getReportName() + "_MaxColumnPerLine")))
							htmlLineAfterRemovingExtraValue = StringUtils.substring(htmlLineAfterRemovingExtraValue, 0,
									Integer.parseInt(
											prop.getProperty(repMetaDataObj.getReportName() + "_MaxColumnPerLine")));

						/*
						 * if(repMetaDataObj.isAlignFirstLineEachRecordIsNeeded()) {
						 * htmlLineAfterRemovingExtraValue=StringUtils.substring(
						 * htmlLineAfterRemovingExtraValue, 7);
						 * repMetaDataObj.setAlignFirstLineEachRecordIsNeeded(false);
						 * 
						 * }
						 */

						if (getTheListOfAllWordsInLine(htmlLineAfterRemovingExtraValue) != null
								&& getTheListOfAllWordsInLine(htmlLineAfterRemovingExtraValue).size() > 0) {
							eachWordInInput = getTheListOfAllWordsInLine(htmlLineAfterRemovingExtraValue).get(0);
//	if(StringUtils.isNoneEmpty(eachWordInInput)&&StringUtils.length(eachWordInInput)>1&&eachWordInInput.charAt(0)=='1')

							if (StringUtils.isNoneEmpty(eachWordInInput) && eachWordInInput.charAt(0) == '1'
									&& StringUtils.isNoneEmpty(finalEachLineHtmlContent))
								finalEachLineHtmlContent = finalEachLineHtmlContent
										+ "<tr><td class=\"page-break-jlp\"></td></tr>";

							if (StringUtils.isNoneEmpty(eachWordInInput)
									&& (eachWordInInput.charAt(0) == '1' || eachWordInInput.charAt(0) == '0'))
								htmlLineAfterRemovingExtraValue = " "
										+ StringUtils.substring(htmlLineAfterRemovingExtraValue, 1);
							// htmlLineAfterRemovingExtraValue
							// =StringUtils.substring(htmlLineAfterRemovingExtraValue, 1);

							//

							if (StringUtils.isNoneEmpty(htmlLineAfterRemovingExtraValue)
									&& (htmlLineAfterRemovingExtraValue.charAt(0) == '-'))

							// if(StringUtils.isNoneEmpty(htmlLineAfterRemovingExtraValue)&&(htmlLineAfterRemovingExtraValue.charAt(0)=='-')&&!hashLineFounInSourceFile)
							{
								htmlLineAfterRemovingExtraValue = " "
										+ StringUtils.substring(htmlLineAfterRemovingExtraValue, 1);
								if (StringUtils.length(htmlLineAfterRemovingExtraValue) > 1) {
									finalEachLineHtmlContent = finalEachLineHtmlContent
											+ "<tr class=\"tr-0\"><td><pre></pre></td></tr>";
									finalEachLineHtmlContent = finalEachLineHtmlContent
											+ "<tr class=\"tr-0\"><td><pre></pre></td></tr>";
									finalEachLineHtmlContent = finalEachLineHtmlContent
											+ "<tr class=\"tr-0\"><td><pre></pre></td></tr>";
								}
							}

							// if(StringUtils.isNoneEmpty(eachWordInInput)&&StringUtils.length(eachWordInInput)>1&&eachWordInInput.charAt(0)=='0')
							// if(StringUtils.isNoneEmpty(eachWordInInput)&&eachWordInInput.charAt(0)=='0')
							// htmlLineAfterRemovingExtraValue =
							// StringUtils.substring(htmlLineAfterRemovingExtraValue, 1);

						}

						if (!StringUtils.isEmpty(htmlLineAfterRemovingExtraValue) && StringUtils.containsIgnoreCase(
								prop.getProperty("Reports_Preserve_Spacing"), repMetaDataObj.getReportName()))
							htmlLineAfterRemovingExtraValue = StringUtils.stripEnd(htmlLineAfterRemovingExtraValue,
									null);

						if (!StringUtils.isEmpty(htmlLineAfterRemovingExtraValue)
								&& !StringUtils.containsIgnoreCase(prop.getProperty("Reports_Preserve_Spacing"),
										repMetaDataObj.getReportName()))
							htmlLineAfterRemovingExtraValue = StringUtils.trim(htmlLineAfterRemovingExtraValue);

						if (StringUtils.equalsAnyIgnoreCase(htmlLineAfterRemovingExtraValue, "-"))
							htmlLineAfterRemovingExtraValue = "";

//if(!StringUtils.isEmpty(htmlLineAfterRemovingExtraValue))
						// htmlLineAfterRemovingExtraValue =
						// StringUtils.strip(htmlLineAfterRemovingExtraValue);

//htmlLineAfterRemovingExtraValue = StringUtils.stripEnd(htmlLineAfterRemovingExtraValue, null);

//finalEachLineHtmlContent=finalEachLineHtmlContent+htmlLineAfterRemovingExtraValue+"<br>";
//if(!StringUtils.isEmpty(htmlLineAfterRemovingExtraValue))
//htmlLineAfterRemovingExtraValue = StringUtils.trim(htmlLineAfterRemovingExtraValue);

						if (StringUtils.isEmpty(htmlLineAfterRemovingExtraValue))
							finalEachLineHtmlContent = finalEachLineHtmlContent
									+ "<tr class=\"tr-0\"><td><pre></pre></td></tr>";
						else
							finalEachLineHtmlContent = finalEachLineHtmlContent + "<tr><td><pre>"
									+ htmlLineAfterRemovingExtraValue + "</tr></td></pre>";

						if (StringUtils.containsIgnoreCase(prop.getProperty("Reports_With_Extra_Space_After_Header"),
								repMetaDataObj.getReportName()) && hashLineFounInSourceFile)
							finalEachLineHtmlContent = finalEachLineHtmlContent
									+ "<tr class=\"tr-0\"><td><pre></pre></td></tr>";

//finalEachLineHtmlContent=finalEachLineHtmlContent+htmlLineAfterRemovingExtraValue;

//finalEachLineHtmlContent=finalEachLineHtmlContent+htmlLineAfterRemovingExtraValue;

						htmlListForTabularStructure.add(htmlLineAfterRemovingExtraValue);
					}

//htmlListForTabularStructure.add("<div class=\"page-break\"></div>");

//if(counter>1)
//finalEachLineHtmlContent=finalEachLineHtmlContent+"<tr><td class=\"page-break-jlp\"></td></tr>";
					// finalEachLineHtmlContent=finalEachLineHtmlContent;
					
					if(counter>1&&pageNumberCounter<listOfEachRecord.size()-1)
					{
					finalEachLineHtmlContent=finalEachLineHtmlContent+"<tr><td class=\"page-break-jlp\"></td></tr>";
					pageNumberCounter++;
					}

				}

//listOfEachLinesWithinAGivenRecord =

			}

		}

		repMetaDataObj.setListOfFinalHtmlLinesForTabular(htmlListForTabularStructure);

		return finalEachLineHtmlContent;
	}

	public void setTheRowValueMapFromInputFile(ReportMetaData repMetaDataObj) {

		String allLinesWithinRecord, htmlLineAfterRemovingExtraValue;
		List<String> listOfEachRecord = repMetaDataObj.getListOfUniqueRecordsInSourceFile();
		List<String> listOfAllWordsInALine, listOfLinesInTheSourceFileToBePtinted, listOfEachLinesWithinAGivenRecord;
		Iterator<String> eachRecordIterator;
		Iterator<String> eachLineWithinRecordIterator;
		int counter = 0, rowCountWithinRecord = 0;
		boolean underLineFoundInTable = false;

		LinkedHashMap<Integer, List<String>> reportNumberMapObj = new LinkedHashMap<>();

		String finalEachLineHtmlContent = "";

		List<String> htmlListForTabularStructure = new ArrayList<String>();

		if (listOfEachRecord != null && listOfEachRecord.size() > 0) {
			eachRecordIterator = listOfEachRecord.iterator();

			while (eachRecordIterator.hasNext()) {
				counter++;
				allLinesWithinRecord = eachRecordIterator.next();

				if (repMetaDataObj.isMaskingNeeded() && counter > 1)
					allLinesWithinRecord = applyMasking(allLinesWithinRecord);

//Arrays.asList(StringUtils.split(allLinesWithinRecord,"\n"));
				listOfEachLinesWithinAGivenRecord = new ArrayList<>();

				listOfEachLinesWithinAGivenRecord.addAll(Arrays.asList(StringUtils.split(allLinesWithinRecord, "\n")));

				rowCountWithinRecord = 0;

				if (listOfEachLinesWithinAGivenRecord != null && listOfEachLinesWithinAGivenRecord.size() > 0) {
					eachLineWithinRecordIterator = listOfEachLinesWithinAGivenRecord.iterator();

					while (eachLineWithinRecordIterator.hasNext()) {
						eachLineWithinRecordIterator.next();

						if (counter == 1)
							continue;

						reportNumberMapObj.put(Integer.valueOf(counter - 1), listOfEachLinesWithinAGivenRecord);
						break;

					}

				}

			}

		}

		repMetaDataObj.setListOfFinalHtmlLinesForTabular(htmlListForTabularStructure);

		repMetaDataObj.setReportNumberMap(reportNumberMapObj);

		return;

	}

	private String createMaskedString(int sizeOfMaskedString) {
		String maskedValue = "";

		for (int i = 0; i < sizeOfMaskedString; i++)
			maskedValue = maskedValue + "X";
		return maskedValue;
	}

	private String applyMasking(String entireHtmlForOneRecord) {
		String maskedValueForThEntireBlock = "", maskedValue;

		String maskedParameter = prop.getProperty("Masking_" + repMetaData.getReportName()), lineContent;
		String[] allParemetersInARecordToBeMasked = StringUtils.split(maskedParameter, "#");

		valuesListBeforeMasking = new ArrayList<String>();

		List listOfEachLinesWithinAGivenRecord = new ArrayList<>();

		listOfEachLinesWithinAGivenRecord.addAll(Arrays.asList(StringUtils.split(entireHtmlForOneRecord, "\n")));

		int lineNumber, columnStarting, columnEnding;

		for (int count = 0; count < allParemetersInARecordToBeMasked.length; count++) {
			lineNumber = Integer.valueOf(StringUtils.substringBefore(allParemetersInARecordToBeMasked[count], ","));
			columnStarting = Integer
					.valueOf(StringUtils.substringBetween(allParemetersInARecordToBeMasked[count], ","));
			columnEnding = Integer
					.valueOf(StringUtils.substringAfterLast(allParemetersInARecordToBeMasked[count], ","));
			lineContent = (String) listOfEachLinesWithinAGivenRecord.get(lineNumber - 1);
			valuesListBeforeMasking.add(lineContent.substring(columnStarting - 1, columnEnding - 1));
			maskedValue = createMaskedString(columnEnding - columnStarting);
			entireHtmlForOneRecord = StringUtils.replace(entireHtmlForOneRecord,
					lineContent.substring(columnStarting - 1, columnEnding - 1), maskedValue);

			repMetaData.setSourceFileContent(StringUtils.replace(repMetaData.getSourceFileContent(),
					lineContent.substring(columnStarting - 1, columnEnding - 1), maskedValue));

		}

		return entireHtmlForOneRecord;
	}

	public String processNormalReport(ReportMetaData repMetaDataObj) {
		String htmlContent = "", singleLineInSourceFile, finalEachLineHtmlContent = "";
		List<String> listOfEachLinesInSourceFile = repMetaDataObj.getListOfEachLinesInFile();
		List<String> listOfAllWordsInALine, listOfLinesInTheSourceFileToBePtinted;
		Iterator eachSourceLineIterator;
		int counter = 0;

		if (listOfEachLinesInSourceFile != null && listOfEachLinesInSourceFile.size() > 0) {
			eachSourceLineIterator = listOfEachLinesInSourceFile.iterator();

			while (eachSourceLineIterator.hasNext()) {
				singleLineInSourceFile = (String) eachSourceLineIterator.next();
				counter++;

				if (counter == 1)
					continue;
//finalEachLineHtmlContent=finalEachLineHtmlContent+singleLineInSourceFile+"<br></br>";

				finalEachLineHtmlContent = finalEachLineHtmlContent + singleLineInSourceFile + "<br></br>";

			}

		}

		if (StringUtils.contains(finalEachLineHtmlContent, " "))
			finalEachLineHtmlContent.replaceAll(" ", "");

		repMetaDataObj.setFinalHtmlContent(finalEachLineHtmlContent);

		return finalEachLineHtmlContent;
	}

	public void replaceDynamicVariablesForAllNonGenericTemplate(ReportMetaData repMetaDataObj) {

		System.out.println("Inside replaceDynamicVariablesForAllNonGenericTemplate");
		String entireHtmlContent = "", htmlContentPerBlock = "", newHtml = "", valueOfKeyFromSourceFile, barCodeSample,
				keyWithHashSymbol, zplInterimContent = "";
		htmlContentPerBlock = StringUtils.substringBetween(repMetaDataObj.getHtmlFileContentWithParameter(),
				"<div id=\"start_of_html_main_segment\"></div>", "<div id=\"end_of_html_main_segment\"></div>");

		MultiValuedMap<String, String> listOfAllKeyValuesForTemplate = repMetaDataObj.getKeyValueOfTemplate();
//reportNumberMap
		LinkedHashMap<Integer, List<String>> reportNumberMapObj = repMetaDataObj.getReportNumberMap();

		int row, columnStarting, columnEnding;

		List<String> listOfAllLinesInAGivenRecord;
		int indexOfEachRecord = 0, countOfEachReacord = 0;

		if (reportNumberMapObj != null && reportNumberMapObj.size() > 0) {
			if (reportNumberMapObj != null && reportNumberMapObj.size() > 0) {
				for (Map.Entry<Integer, List<String>> reportNumberMapObjEntry : reportNumberMapObj.entrySet()) {
					countOfEachReacord++;
					listOfAllLinesInAGivenRecord = reportNumberMapObjEntry.getValue();
					htmlContentPerBlock = StringUtils.substringBetween(repMetaDataObj.getHtmlFileContentWithParameter(),
							"<div id=\"start_of_html_main_segment\"></div>",
							"<div id=\"end_of_html_main_segment\"></div>");

					if (listOfAllKeyValuesForTemplate != null && listOfAllKeyValuesForTemplate.size() > 0) {
						for (Map.Entry<String, String> entry : listOfAllKeyValuesForTemplate.entries()) {

							String[] listWithRowColumnValue = StringUtils.split(entry.getKey(), "_");
							row = Integer.valueOf(listWithRowColumnValue[1]) - 1;
							columnStarting = Integer.valueOf(listWithRowColumnValue[2]) - 1;
							columnEnding = Integer.valueOf(listWithRowColumnValue[3]) - 1;

							if (row > listOfAllLinesInAGivenRecord.size() - 1) {
								keyWithHashSymbol = entry.getKey() + "#";
								// htmlContentPerBlock=StringUtils.replaceIgnoreCase(htmlContentPerBlock,
								// entry.getKey(), "");
								htmlContentPerBlock = StringUtils.replaceIgnoreCase(htmlContentPerBlock,
										keyWithHashSymbol, "");

								continue;
							}
							valueOfKeyFromSourceFile = StringUtils.substring(listOfAllLinesInAGivenRecord.get(row),
									columnStarting, columnEnding);

							if (StringUtils.containsIgnoreCase(htmlContentPerBlock,
									"<barcode>" + entry.getKey() + "#</barcode>")) {
								createBarCode(valueOfKeyFromSourceFile);
								barCodeSample = "<img src=\"" + valueOfKeyFromSourceFile + ".gif\"/>";
								htmlContentPerBlock = StringUtils.replace(htmlContentPerBlock,
										"<barcode>" + entry.getKey() + "#</barcode>", barCodeSample);
								continue;

							}

							if (StringUtils.containsIgnoreCase(htmlContentPerBlock, "<SingleSpace></SingleSpace>")) {
								htmlContentPerBlock = StringUtils.replace(htmlContentPerBlock,
										"<SingleSpace></SingleSpace>", " ");

							}

							htmlContentPerBlock = StringUtils.replace(htmlContentPerBlock, entry.getKey() + "#",
									StringUtils.substring(listOfAllLinesInAGivenRecord.get(row), columnStarting,
											columnEnding));
							// entireHtmlContent =
							// entireHtmlContent+StringUtils.substring(listOfAllLinesInAGivenRecord.get(row),
							// columnStarting, columnEnding);

							// repMetaDataObj.setFinalHtmlContent(StringUtils.replace(repMetaDataObj.getHtmlFileContentWithParameter(),
							// entry.getKey(), entry.getValue()));
						}

					}

					// System.out.println("aaa");

					if (countOfEachReacord <= reportNumberMapObj.size() - 1)
						newHtml = newHtml + htmlContentPerBlock + "<div class=\"page-break-jlp\"></div>";
					else
						newHtml = newHtml + htmlContentPerBlock;

					generateZPLOutput(repMetaDataObj, reportNumberMapObjEntry);

					if (indexOfEachRecord > 0) {
						zplInterimContent = StringUtils.substringBefore(zplInterimContent, "^XZ")
								+ repMetaDataObj.getZplOutputFileContent();
					} else {
						zplInterimContent = repMetaDataObj.getZplOutputFileContent();
					}

					indexOfEachRecord++;

					// newHtml = newHtml+htmlContentPerBlock;

					// repMetaDataObj.setFinalHtmlContent(StringUtils.replace(repMetaDataObj.getHtmlFileContentWithParameter(),
					// entry.getKey(), entry.getValue()));
				}

				// "<div class=\"page-break-jlp\"></div>";
			}

		}

		entireHtmlContent = StringUtils.substringBefore(repMetaDataObj.getHtmlFileContentWithParameter(),
				"<div id=\"start_of_html_main_segment\"></div>") + newHtml
				+ StringUtils.substringAfter(repMetaDataObj.getHtmlFileContentWithParameter(),
						"<div id=\"end_of_html_main_segment\"></div>");
		repMetaDataObj.setFinalHtmlContent(entireHtmlContent);
		zplInterimContent = "^XA" + StringUtils.replace(zplInterimContent, "^XA", "");
		repMetaDataObj.setZplOutputFileContent(zplInterimContent);

		return;

	}

	private void generateZPLOutput(ReportMetaData repMetaDataObj,
			Map.Entry<Integer, List<String>> reportNumberMapObjEntry) {

		String entireHtmlContent = "", newHtml = "", valueOfKeyFromSourceFile, barCodeSample, keyWithHashSymbol,
				tmpZplTemplateContent;

		System.out.println();

		MultiValuedMap<String, String> listOfAllKeyValuesForTemplate = repMetaDataObj.getKeyValueOfZPLTemplate();
//reportNumberMap
		// LinkedHashMap<Integer,List<String>> reportNumberMapObj =
		// repMetaDataObj.getReportNumberMap();

		int row, columnStarting, columnEnding;

		List<String> listOfAllLinesInAGivenRecord;

		tmpZplTemplateContent = repMetaDataObj.getZplTemplateContent();

		listOfAllLinesInAGivenRecord = reportNumberMapObjEntry.getValue();

		if (listOfAllKeyValuesForTemplate != null && listOfAllKeyValuesForTemplate.size() > 0) {
			for (Map.Entry<String, String> entry : listOfAllKeyValuesForTemplate.entries()) {

				String[] listWithRowColumnValue = StringUtils.split(entry.getKey(), "_");
				row = Integer.valueOf(listWithRowColumnValue[1]) - 1;
				columnStarting = Integer.valueOf(listWithRowColumnValue[2]) - 1;
				columnEnding = Integer.valueOf(listWithRowColumnValue[3]) - 1;

				if (row > listOfAllLinesInAGivenRecord.size() - 1) {

					// htmlContentPerBlock=StringUtils.replaceIgnoreCase(htmlContentPerBlock,
					// entry.getKey(), "");

					continue;
				}
				valueOfKeyFromSourceFile = StringUtils.substring(listOfAllLinesInAGivenRecord.get(row), columnStarting,
						columnEnding);
				keyWithHashSymbol = entry.getKey() + "#";

				repMetaDataObj.setZplTemplateContent(StringUtils.replace(repMetaDataObj.getZplTemplateContent(),
						keyWithHashSymbol, valueOfKeyFromSourceFile));

			}

		}

		repMetaDataObj.setZplOutputFileContent(repMetaDataObj.getZplTemplateContent());

		repMetaDataObj.setZplTemplateContent(tmpZplTemplateContent);

		return;

	}

	public String replaceDynamicVariableInTemplate(ReportMetaData repMetaDataObj) {
		String finalHtmlContent = null;

		MultiValuedMap<String, String> listOfAllKeyValuesForTemplate = repMetaDataObj.getKeyValueOfTemplate();

		if (listOfAllKeyValuesForTemplate != null && listOfAllKeyValuesForTemplate.size() > 0) {
			for (Map.Entry<String, String> entry : listOfAllKeyValuesForTemplate.entries()) {

				repMetaDataObj.setFinalHtmlContent(StringUtils.replace(repMetaDataObj.getHtmlFileContentWithParameter(),
						entry.getKey(), entry.getValue()));
			}

		}

		finalHtmlContent = repMetaDataObj.getFinalHtmlContent();

		return finalHtmlContent;
	}

	private String getTheExactTransactionValueFromSourceFile(int row, int startColumn, int endColumn) {

		String value = "", firstRecord = null, rowValue = "";

		List firstRecordList = new ArrayList<String>(), listOfAllRowsInsideRecord;

		if (repMetaData.getListOfUniqueRecordsInSourceFile() != null
				&& repMetaData.getListOfUniqueRecordsInSourceFile().size() >= row) {

			if (repMetaData.getListOfUniqueRecordsInSourceFile().size() == 1) {
				rowValue = (String) repMetaData.getListOfUniqueRecordsInSourceFile().get(0);
				firstRecord = rowValue;
			} else {

				if (repMetaData.isTabularReport()
						&& StringUtils.containsIgnoreCase(repMetaData.getReportName(), "CDSPRINTMAN"))
					firstRecord = (String) repMetaData.getListOfUniqueRecordsInSourceFile().get(2);
				else
					firstRecord = (String) repMetaData.getListOfUniqueRecordsInSourceFile().get(1);
			}

			firstRecordList.add(Arrays.asList(StringUtils.split(firstRecord, "\n")));
			listOfAllRowsInsideRecord = (List) firstRecordList.get(0);

			if (StringUtils.isEmpty(rowValue)) {

				if (row == -1)
					rowValue = repMetaData.getHeaderValue();
				else
					rowValue = (String) listOfAllRowsInsideRecord.get(row);

			}

			value = StringUtils.substring(rowValue, startColumn, endColumn);
//System.out.println((String) repMetaData.getListOfUniqueRecordsInSourceFile().get(2));

		}

		return value;

	}

	int getIndexOfGivenWordInLineList(String inputLine, String stringToBeLocated) {

		List<String> listOfAllWordsInALine;
		listOfAllWordsInALine = getTheListOfAllWordsInLine(inputLine);

		return listOfAllWordsInALine.indexOf(stringToBeLocated);

	}

	private List getTheUniqueBarCodeIdentifier(List listOfAllWordsInALine, String barCodeIdentifier) {
		List<String> listOfUniqueBarCodeIdentifier = new ArrayList<String>();

		Iterator<String> wordIter = listOfAllWordsInALine.iterator();

		String eachWord, barCodeIdentifierValue;
		int indexCounter = 0;

		while (wordIter.hasNext()) {
			eachWord = wordIter.next();

			if (repMetaData.isTabularReport() && StringUtils.equalsAnyIgnoreCase(eachWord, barCodeIdentifier)
					&& StringUtils.isNotEmpty((String) listOfAllWordsInALine.get(indexCounter + 1))
					&& !listOfUniqueBarCodeIdentifier.contains((String) listOfAllWordsInALine.get(indexCounter + 1))) {

				listOfUniqueBarCodeIdentifier.add((String) listOfAllWordsInALine.get(indexCounter + 1));
			}
			if (repMetaData.isTabularReport() && StringUtils.containsAnyIgnoreCase(eachWord, barCodeIdentifier)
					&& !listOfUniqueBarCodeIdentifier.contains(eachWord)) {
				eachWord = StringUtils.substringBefore(eachWord, barCodeIdentifier);
				listOfUniqueBarCodeIdentifier.add(eachWord);
			}
			indexCounter++;
		}

		return listOfUniqueBarCodeIdentifier;
	}

	public boolean copyTheFileToADirectory(String destinationFolderAbsPath, String absPathOfFile) {
		boolean fileCopied = false;

		File directoryObj = new File(destinationFolderAbsPath), fileObjAbsPath;

		if (!directoryObj.exists())
			directoryObj.mkdirs();
		else {
			fileObjAbsPath = new File(
					destinationFolderAbsPath + "/" + StringUtils.substringAfterLast(absPathOfFile, "/"));

			if (!fileObjAbsPath.exists()) {
				createFile(fileObjAbsPath.getAbsolutePath(), readFileContent(absPathOfFile));
				fileCopied = true;
			} else {
				return fileCopied;
			}
		}
		return fileCopied;
	}

	public void insertBarCodeToHtmlForTabularReport(String barCodeIdentifier) {
		String entireHtmlContent = repMetaData.getFinalHtmlContent();

//if(repMetaData.isTabularReport())
//barCodeIdentifier=String.valueOf(barCodeIdentifier.charAt(1));

		String[] listOfLinesBetweenIdentifiers;
		String eachHtmlLine, eachWordInTheInput, barCodeValue = "", barCodeSample;
		List<String> listOfAllWordsInALine;
		int indexOfBarCodeKey, indexOfBarCodeValue = -1, numberOfLinesToBeInsertedToFillPage;
		List<String> listOfUniqueBarCodeIdentifier = new ArrayList<String>(), listOfAllDynamicValues = null;

//StringUtils.repl
		List<String> htmlContentLinesList = fileContentAsLineList(entireHtmlContent);

//if(StringUtils.containsIgnoreCase(entireHtmlContent, barCodeIdentifier)&&repMetaData.getReportType().equalsIgnoreCase("MQ_Tabular"))

//String tmp;
		for (int i = 0; i < htmlContentLinesList.size(); i++) {
			eachHtmlLine = (String) htmlContentLinesList.get(i);

			listOfAllWordsInALine = getTheListOfAllWordsInLineWithSpaceAsDelimiter(eachHtmlLine);

			if ((StringUtils.containsIgnoreCase(eachHtmlLine, barCodeIdentifier) && i > 0)
					|| (StringUtils.contains(eachHtmlLine, barCodeIdentifier) && i > 0)) {
				listOfAllDynamicValues = getTheUniqueBarCodeIdentifier(listOfAllWordsInALine, barCodeIdentifier);
			}

		}
		String eachBarCodeValue = null, barCodeWithAppropriateBlankLines = null;
		if (listOfAllDynamicValues != null && listOfAllDynamicValues.size() > 0) {
			Iterator<String> dynamicValueIter = listOfAllDynamicValues.iterator();

			while (dynamicValueIter.hasNext()) {
				eachBarCodeValue = dynamicValueIter.next();
				createBarCode(eachBarCodeValue);
				barCodeSample = "<img src=\"" + eachBarCodeValue + ".gif\" />";

//if(StringUtils.isNoneBlank(repMetaData.getBarCodePosition()))

//if(StringUtils.isNoneBlank(repMetaData.getBarCodePosition())&&StringUtils.containsIgnoreCase(repMetaData.getBarCodePosition(), "top"))
//barCodeSample=barCodeSample+"<div class=\"page-break-jlp\">";

//entireHtmlContent = StringUtils.replaceIgnoreCase(entireHtmlContent, eachBarCodeValue, barCodeSample,1);
//entireHtmlContent = StringUtils.replaceIgnoreCase(entireHtmlContent, eachBarCodeValue, barCodeSample,1);

				entireHtmlContent = StringUtils.replaceIgnoreCase(entireHtmlContent,
						"<tr><td><pre>" + eachBarCodeValue + "</tr></td></pre>",
						"<tr align=\"center\"><td><pre>" + barCodeSample + "</tr></td></pre>", 1);

			}

//barCodeSample="<img src=\"123.gif\" class=\"center\"/>"

		}

//if(repMetaData.isTabularReport())
//entireHtmlContent=StringUtils.replaceAll(entireHtmlContent, barCodeIdentifier, "");

		repMetaData.setFinalHtmlContent(entireHtmlContent);
	}

	public void populateTransactionVariables(ReportMetaData repMetaDataObj) {
		String transactionVariables = prop
				.getProperty(repMetaDataObj.getKeyPrefix() + repMetaDataObj.getReportName() + "_transactionVariables"),
				rowColumnDefn;

		List transactionVariablesList = new ArrayList<String>(), listOfAllTransactionVariable,
				rowColumnList = new ArrayList<String>(), listOfRowColumn;

		int row, startColumn, endColumn;

		String eachDynamicVariableName, key;

		Iterator<String> transactionVariableListIter;
		if (StringUtils.isNotEmpty(transactionVariables)) {

			transactionVariablesList.add(Arrays.asList(StringUtils.split(transactionVariables, ",")));
			listOfAllTransactionVariable = (List) transactionVariablesList.get(0);

			if (listOfAllTransactionVariable.size() > 0) {
//transactionVariablesList.it
				transactionVariableListIter = listOfAllTransactionVariable.iterator();
				while (transactionVariableListIter.hasNext()) {
					eachDynamicVariableName = transactionVariableListIter.next();
					key = repMetaDataObj.getKeyPrefix() + repMetaDataObj.getReportName() + "_transactionVariable_"
							+ eachDynamicVariableName;

					rowColumnDefn = prop.getProperty(key);

					if (rowColumnDefn == null)
						continue;

					rowColumnList.add(Arrays.asList(StringUtils.split(rowColumnDefn, ",")));
					listOfRowColumn = (List) rowColumnList.get(rowColumnList.size() - 1);

					row = Integer.valueOf((String) listOfRowColumn.get(0));
					startColumn = Integer.valueOf((String) listOfRowColumn.get(1));
					endColumn = Integer.valueOf((String) listOfRowColumn.get(2));

//getTheExactTransactionValueFromSourceFile(row, startColumn, endColumn);

					repMetaDataObj.getTransactionVariableList().put(eachDynamicVariableName,
							getTheExactTransactionValueFromSourceFile(row, startColumn, endColumn));

					//System.out.println("Transaction Variable==" + repMetaDataObj.getTransactionVariableList());
				}
			}

		}

			}

	String createHeaderFormatForMFTReport(String cbsHeaderValueFromAInputFile) {
		String cbsHeaderValue = "", firstOccuranceOfInteger = null,
				cbsHeaderValueFromAInputFileAfterRemovingSpaces = "";
		int indexOfFirstInteger = 0;

		if (StringUtils.isNotEmpty(cbsHeaderValueFromAInputFile)
				&& StringUtils.length(cbsHeaderValueFromAInputFile) > 3) {
			cbsHeaderValueFromAInputFileAfterRemovingSpaces = StringUtils.substringBefore(cbsHeaderValueFromAInputFile,
					" ");

			Matcher m = Pattern.compile("[^0-9]*([0-9]+).*").matcher(cbsHeaderValueFromAInputFileAfterRemovingSpaces);
			if (m.matches()) {
				firstOccuranceOfInteger = m.group(1);
				indexOfFirstInteger = StringUtils.indexOf(cbsHeaderValueFromAInputFileAfterRemovingSpaces,
						firstOccuranceOfInteger);

			}
			cbsHeaderValue = StringUtils.substringBefore(cbsHeaderValueFromAInputFileAfterRemovingSpaces,
					firstOccuranceOfInteger) + "/"
					+ StringUtils.substring(cbsHeaderValueFromAInputFileAfterRemovingSpaces, indexOfFirstInteger,
							indexOfFirstInteger + 3)
					+ "/";

		}
		return cbsHeaderValue;
	}

	public MultiValuedMap<String, String> populateDynamicVariablesForZPLTemplate(String zplTemplateContent) {
		List listOfAllLinesInTemplate = fileContentAsLineList(zplTemplateContent);
		List<String> listOfAllWordsInALine;
		MultiValuedMap<String, String> keyValueOfDynamicVariables = new ArrayListValuedHashMap<String, String>();

		// MultiValuedMap ob = new

		String singleLineInFile;

		Iterator eachLineIterator;

		if (listOfAllLinesInTemplate != null && listOfAllLinesInTemplate.size() > 0) {
			eachLineIterator = listOfAllLinesInTemplate.iterator();

			while (eachLineIterator.hasNext()) {
				singleLineInFile = (String) eachLineIterator.next();

				if (StringUtils.containsIgnoreCase(singleLineInFile, "$Row")) {
					String[] listWithRowColumnValue = singleLineInFile.split("\\$Row_");// StringUtils.split(entry,"Row");
					// ArrayList newList = new ArrayList<>();

					for (int i = 0; i < listWithRowColumnValue.length; i++) {
						if (i == 0) {
							continue;
						}
						// newList.add("$Row_"+listWithRowColumnValue[i].substring(0,
						// listWithRowColumnValue[i].indexOf("#")));
						// repMetaDataObj.setKeyValueOfTemplate("$Row_"+listWithRowColumnValue[i].substring(0,
						// listWithRowColumnValue[i].indexOf("#")), null);
						// repMetaDataObj.setSimpleTabularDataComingInSource(true);

						keyValueOfDynamicVariables.put("$Row_"
								+ listWithRowColumnValue[i].substring(0, listWithRowColumnValue[i].indexOf("#")), null);
					}
					continue;
				}

			}

		}
		return keyValueOfDynamicVariables;

	}

	public ReportMetaData populateReportMetaData(String sourceFileAbsPath) {

		String reportName = null, asciFileContent = null, templateFileContent = null;

		boolean isInputFileCBS = true, alignLineToLeftIsNeeded = false;
		int headerRowCount = 0;

		if (StringUtils.containsIgnoreCase(sourceFileAbsPath, "mq")) {
			isInputFileCBS = false;
		}
		asciFileContent = readEntireFileIntoString(sourceFileAbsPath);

		if (asciFileContent.contains("$"))
			asciFileContent = StringUtils.replace(asciFileContent, "$", "£");

		repMetaData = new ReportMetaData();
		repMetaData.setTabularReport(true);
		repMetaData.setHold(false);
		repMetaData.setAlignFirstLineEachRecordIsNeeded(false);
		repMetaData.setCbsFilePresentInTheSource(isInputFileCBS);
		repMetaData.setMaskingNeeded(false);

		repMetaData.setSourceFileContent(asciFileContent);

//repMetaData.setLL

		String[] eachAsciiInputFileContentList;

		if (isInputFileCBS) {
//eachAsciiInputFileContentList=asciFileContent.split("1\\?");
//eachAsciiInputFileContentList=asciFileContent.split("\\?1\\?");
			eachAsciiInputFileContentList = asciFileContent.split("\\?1");

			repMetaData.setTabularReport(false);

		} else {
//eachAsciiInputFileContentList=asciFileContent.split(" ");
			eachAsciiInputFileContentList = asciFileContent.split("");
			repMetaData.setAlignFirstLineEachRecordIsNeeded(true);

		}

		String headerValue = null;

		List<String> inputFileRecordList = Arrays.asList(eachAsciiInputFileContentList);
		repMetaData.setListOfUniqueRecordsInSourceFile(inputFileRecordList);
//if(StringUtils.isNotEmpty(prop.getProperty("MaskingNeeded"))&&StringUtils.equalsIgnoreCase(prop.getProperty("MaskingNeeded"), "yes")&&StringUtils.containsIgnoreCase(prop.getProperty("MaskingReportNames"), repMetaData.getReportName()))
//{
//
//	repMetaData.setMaskingNeeded(true);
//
//}
//setTheRowValueMapFromInputFile(repMetaData);
//

		String eachReacordofInputFile;
		int counter = 0;
		if (isInputFileCBS)
			headerRowCount = 0;

		if (inputFileRecordList != null && inputFileRecordList.size() > 0) {
			Iterator<String> asciiInputLineIter = inputFileRecordList.iterator();

			while (asciiInputLineIter.hasNext()) {
				eachReacordofInputFile = asciiInputLineIter.next();

				if (counter == headerRowCount) {

//headerValue=eachReacordofInputFile;
					headerValue = StringUtils.substring(eachReacordofInputFile, 0, 100);

					if (StringUtils.isNotBlank(headerValue) && !StringUtils.contains(headerValue, "/")) {
						headerValue = createHeaderFormatForMFTReport(headerValue);
					}

					if (StringUtils.isNotBlank(headerValue) && StringUtils.contains(headerValue, "/")) {

						reportName = StringUtils.deleteWhitespace(StringUtils.substringBefore(headerValue, "/"));

						if (!isInputFileCBS)
							reportName = reportName.substring(1);

						if (StringUtils.startsWith(reportName, "?"))
							reportName = StringUtils.replace(reportName, "?", "");

						String[] eachTokens = headerValue.split("/");

						repMetaData.setReportName(reportName);
						repMetaData.setBranchId(eachTokens[1]);

						if (StringUtils.isNotEmpty(prop.getProperty("MaskingNeeded"))
								&& StringUtils.equalsIgnoreCase(prop.getProperty("MaskingNeeded"), "yes")
								&& StringUtils.containsIgnoreCase(prop.getProperty("MaskingReportNames"),
										repMetaData.getReportName())) {

							repMetaData.setMaskingNeeded(true);

						}
						setTheRowValueMapFromInputFile(repMetaData);

						if (!StringUtils.isAlphanumeric(reportName) || !StringUtils.isAlphanumeric(eachTokens[1])) {
							repMetaData.setReportName("");
							repMetaData.setBranchId("");
							break;
						}

						repMetaData.setListOfEachLinesInFile(fileContentAsLineList(asciFileContent));

						if (!isInputFileCBS) {

							repMetaData.setHtmlFileContentWithParameter(
									readEntireFileIntoString(prop.getProperty("SourceDirectoryOfTemplate")
											+ prop.getProperty("MQ_" + reportName)));
//repMetaData.setListOfUniqueRecordsInCBSSourceFile(eachReacordofCbs);

						} else {
							repMetaData.setHtmlFileContentWithParameter(
									readEntireFileIntoString(prop.getProperty("SourceDirectoryOfTemplate")
											+ prop.getProperty("CBS_" + reportName)));

						}

						if (StringUtils.isNoneBlank(readEntireFileIntoString(
								prop.getProperty("SourceDirectoryOfTemplate") + "zpl_" + reportName + ".html"))) {
							repMetaData.setZplTemplateContent(readEntireFileIntoString(
									prop.getProperty("SourceDirectoryOfTemplate") + "zpl_" + reportName + ".html"));
							repMetaData.keyValueOfZPLTemplate = populateDynamicVariablesForZPLTemplate(
									repMetaData.getZplTemplateContent());

						}

						if (StringUtils.isNoneBlank(readEntireFileIntoString(
								prop.getProperty("SourceDirectoryOfTemplate") + prop.getProperty("ZPL_" + reportName))))
							repMetaData.setZplTemplateContent(
									readEntireFileIntoString(prop.getProperty("SourceDirectoryOfTemplate")
											+ prop.getProperty("ZPL_" + reportName)));

						templateFileContent = repMetaData.getHtmlFileContentWithParameter();
						repMetaData.setListOfAllWordsInTemplateFile(fileContentAsLineList(templateFileContent));
					}

					if (StringUtils.isBlank(repMetaData.getBranchId())
							|| StringUtils.isBlank(repMetaData.getReportName())) {

						return repMetaData;
					}

//
//if(StringUtils.isNotEmpty(prop.getProperty("MaskingNeeded"))&&StringUtils.equalsIgnoreCase(prop.getProperty("MaskingNeeded"), "yes")&&StringUtils.containsIgnoreCase(prop.getProperty("MaskingReportNames"), repMetaData.getReportName()))
//{
//
//	repMetaData.setMaskingNeeded(true);
//
//}
//setTheRowValueMapFromInputFile(repMetaData);

					break;

				}

				counter++;
			}
		}

		if (StringUtils.isNotEmpty(
				prop.getProperty(repMetaData.getKeyPrefix() + repMetaData.getReportName() + "_barCodeDefinition"))) {

			repMetaData.setBarCodeDefinition(
					prop.getProperty(repMetaData.getKeyPrefix() + repMetaData.getReportName() + "_barCodeDefinition"));
			repMetaData.setBarCodeNeeded(true);

		}

		return repMetaData;
	}

	public List fileContentAsLineList(String fileContent) {
		List listOfLines = new ArrayList<String>();

		String[] eachLineList = fileContent.split("\n");

		listOfLines.addAll(Arrays.asList(eachLineList));

		return listOfLines;
	}

	String readEntireFileIntoString(String absPathOfFile) {
		String fileContent = "";

		try {
			File fileObj = new File(absPathOfFile);
			Scanner fileReader = new Scanner(fileObj);
			while (fileReader.hasNextLine()) {
				String data = fileReader.nextLine();
				data = data + "\n";
				fileContent = fileContent + data;
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred. while writing file into drive");
			e.printStackTrace();
		}

		return fileContent;
	}

	public String readFileContent(String absPathOfFileName) {
		Path fileName = Path.of(absPathOfFileName);

		String htmlFileContent = null;
		try {
			htmlFileContent = Files.readString(fileName);
		} catch (IOException e) {
// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return htmlFileContent;
	}

	String readInputFileContent(String absPathOfFileName) {
		Path fileName = Path.of(absPathOfFileName);

		String htmlFileContent = "";
		try {
			File myObj = new File(absPathOfFileName);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				htmlFileContent = htmlFileContent + data;
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred while reading input file");
			e.printStackTrace();
		}

		return htmlFileContent;
	}

	public static Properties readPropertiesFile() {

		String fileName = "/pmu_config/properties/jlp.properties";
		FileInputStream fis = null;
		prop = null;
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);
			fis.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {

		}
		return prop;
	}

}