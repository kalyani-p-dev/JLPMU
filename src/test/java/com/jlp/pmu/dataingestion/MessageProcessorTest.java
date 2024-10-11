/*
 * package com.jlp.pmu.dataingestion;
 * 
 * import static org.junit.Assert.assertTrue; import static
 * org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.mockito.ArgumentMatchers.any; import static
 * org.mockito.ArgumentMatchers.anyString; import static
 * org.mockito.Mockito.doCallRealMethod; import static
 * org.mockito.Mockito.doNothing; import static org.mockito.Mockito.times;
 * import static org.mockito.Mockito.verify; import static
 * org.mockito.Mockito.when;
 * 
 * import java.io.File; import java.io.FileNotFoundException; import
 * java.io.IOException; import java.lang.reflect.InvocationTargetException;
 * import java.lang.reflect.Method; import java.util.ArrayList; import
 * java.util.Arrays; import java.util.HashMap; import java.util.List; import
 * java.util.Map; import java.util.Properties;
 * 
 * import org.junit.jupiter.api.Test; import
 * org.junit.jupiter.api.extension.ExtendWith; import org.junit.runner.RunWith;
 * import org.mockito.InjectMocks; import org.mockito.Mock; import
 * org.mockito.junit.jupiter.MockitoExtension; import
 * org.springframework.boot.test.context.SpringBootTest;
 * 
 * import com.jlp.pmu.pojo.ReportMetaData; import com.jlp.pmu.utility.Utility;
 * 
 * @SpringBootTest
 * 
 * @ExtendWith(MockitoExtension.class) class MessageProcessorTest{
 * 
 * @Mock Properties propObj=null;
 * 
 * @Mock ReportMetaData repMetaDataObj= null;
 * 
 * @Mock Utility utilObj =null;
 * 
 * @InjectMocks MessageProcessor messageProcessor = new MessageProcessor();
 * 
 * 
 * //processAllCBSContent
 * 
 * @Test void processAllCBSContent_test() throws NoSuchMethodException,
 * SecurityException, IllegalAccessException, IllegalArgumentException,
 * InvocationTargetException { List inputRecords =
 * Arrays.asList("VDLHDR23455 ?1 PICK NO: ? 787545","SSMCDPLABELS2e ? 1"
 * ,"Order: ? 6357720170");
 * 
 * when(repMetaDataObj.getListOfUniqueRecordsInSourceFile()).thenReturn(
 * inputRecords); when(repMetaDataObj.getReportName()).thenReturn("VDLHDR");
 * when(utilObj.isLineContainsString(anyString(),anyString())).thenReturn(false)
 * .thenReturn(true).thenReturn(false);
 * when(propObj.getProperty("CBS_VDLHDR_exclusionList")).
 * thenReturn("SSMCDPLABELS2e,/c NNe /");
 * 
 * Method method =
 * MessageProcessor.class.getDeclaredMethod("processAllCBSContent");
 * method.setAccessible(true);
 * 
 * String output = (String) method.invoke(messageProcessor);
 * 
 * assertEquals("PICK NO:<br></br>787545<br></br><br></br>Order:<br></br>6357720170<br></br><br></br>"
 * , output);
 * 
 * 
 * }
 * 
 * //appendHtmlContentInEachLine
 * 
 * @Test void appendHtmlContentInEachLine_test() throws NoSuchMethodException,
 * SecurityException, IllegalAccessException, IllegalArgumentException,
 * InvocationTargetException { List input = Arrays.asList(
 * " "," PICK NO:  787545 ", "      6357720170&  ");
 * 
 * Method method =
 * MessageProcessor.class.getDeclaredMethod("appendHtmlContentInEachLine",
 * List.class); method.setAccessible(true);
 * 
 * String output = (String) method.invoke(messageProcessor, input);
 * 
 * assertEquals("PICK NO:  787545<br></br>6357720170&amp;<br></br>", output); }
 * 
 * //processEachToken
 * 
 * @Test void processEachToken_for_mq_tabular_test() throws
 * NoSuchMethodException, SecurityException, IllegalAccessException,
 * IllegalArgumentException, InvocationTargetException {
 * 
 * 
 * when(repMetaDataObj.getKeyPrefix()).thenReturn("MQ_");
 * when(repMetaDataObj.getReportName()).thenReturn("CDSPRINTMAN");
 * when(repMetaDataObj.getReportName()).thenReturn("CDSPRINTMAN");
 * 
 * when(propObj.getProperty("MQ_CDSPRINTMAN_mqContent")).thenReturn(
 * "generateAll");
 * when(propObj.getProperty("ReportNameWithTabularStructure")).thenReturn(
 * "CDSPRINTMAN,LAPHDR,SRCHDR");
 * 
 * repMetaDataObj.isCbsFilePresentInTheSource = false;
 * 
 * when(utilObj.generateTabularReport_new(repMetaDataObj)).
 * thenReturn("fCDSPRTMAN01       Route Manifest THEALE          RouteID: R43AM20230922T1"
 * );
 * 
 * 
 * Method method = MessageProcessor.class.getDeclaredMethod("processEachToken",
 * List.class); method.setAccessible(true);
 * 
 * List<String> input = new ArrayList<>(); input.add("<html>");
 * input.add("Array"); input.add("$mqContent");
 * 
 * method.invoke(messageProcessor, input);
 * 
 * verify(utilObj, times(1)).generateTabularReport_new(repMetaDataObj);
 * 
 * }
 * 
 * 
 * @Test void processEachToken_for_mq_normal_test() throws
 * NoSuchMethodException, SecurityException, IllegalAccessException,
 * IllegalArgumentException, InvocationTargetException {
 * 
 * 
 * when(repMetaDataObj.getKeyPrefix()).thenReturn("MQ_");
 * when(repMetaDataObj.getReportName()).thenReturn("CDSPICKUP");
 * 
 * when(propObj.getProperty("MQ_CDSPICKUP_mqContent")).thenReturn("generateAll")
 * ; when(propObj.getProperty("ReportNameWithTabularStructure")).thenReturn(
 * "CDSPRINTMAN,LAPHDR,SRCHDR");
 * 
 * repMetaDataObj.isCbsFilePresentInTheSource = false;
 * 
 * when(utilObj.processNormalReport(repMetaDataObj)).
 * thenReturn("fCDSPRTMAN01       Route Manifest THEALE          RouteID: R43AM20230922T1"
 * );
 * 
 * 
 * Method method = MessageProcessor.class.getDeclaredMethod("processEachToken",
 * List.class); method.setAccessible(true);
 * 
 * List<String> input = new ArrayList<>(); input.add("<html>");
 * input.add("Array"); input.add("$mqContent");
 * 
 * method.invoke(messageProcessor, input);
 * 
 * verify(utilObj, times(1)).processNormalReport(repMetaDataObj);
 * 
 * }
 * 
 * @Test void processEachToken_for_cbs_normal_test() throws Exception {
 * 
 * 
 * when(repMetaDataObj.getKeyPrefix()).thenReturn("CBS_");
 * when(repMetaDataObj.getReportName()).thenReturn("VDLHDR");
 * repMetaDataObj.isCbsFilePresentInTheSource = true;
 * when(propObj.getProperty("CBS_VDLHDR_cbsContent")).thenReturn("generateAll");
 * 
 * 
 * 
 * Method method = MessageProcessor.class.getDeclaredMethod("processEachToken",
 * List.class); method.setAccessible(true);
 * 
 * List<String> input = new ArrayList<>(); input.add("<html>");
 * input.add("Array"); input.add("$cbsContent");
 * 
 * method.invoke(messageProcessor, input);
 * 
 * 
 * }
 * 
 * 
 * //processInputFile
 * 
 * @Test void processInputFile_normal_test() throws FileNotFoundException,
 * IOException {
 * 
 * Map<String, String> map= new HashMap<>(); map.put("mq", "hi");
 * 
 * when(utilObj.populateReportMetaData(anyString())).thenReturn(repMetaDataObj);
 * when(repMetaDataObj.getBranchId()).thenReturn("029");
 * when(repMetaDataObj.getReportName()).thenReturn("CDSPRINTMAN");
 * when(repMetaDataObj.getHtmlFileContentWithParameter()).
 * thenReturn("Html content");
 * when(utilObj.fileContentAsLineList(anyString())).thenReturn(Arrays.asList(
 * "Hello", "John", "Lewis"));
 * when(utilObj.replaceDynamicVariableInTemplate(any(ReportMetaData.class))).
 * thenReturn(null);
 * doNothing().when(utilObj).populateTransactionVariables(repMetaDataObj);
 * when(utilObj.replaceSpecialCharacterWithEmptySpace(anyString(),
 * anyString())).thenReturn("Hello", "world");
 * when(propObj.getProperty("PdfExcusionCharacters")).thenReturn("Hi");
 * utilObj.repMetaData = repMetaDataObj;
 * doNothing().when(repMetaDataObj).setFinalHtmlContent(anyString());
 * when(repMetaDataObj.isBarCodeNeeded()).thenReturn(true);
 * when(repMetaDataObj.getBarCodeDefinition()).thenReturn("Barcode");
 * doNothing().when(utilObj).insertBarCodeToHtmlForTabularReport("Barcode");
 * when(propObj.getProperty("htmlOutputDirectory")).thenReturn(
 * "C:\\PMURewrite\\JUnit\\output\\");
 * when(repMetaDataObj.getFinalHtmlContent()).thenReturn("<h1>Hello</h1>");
 * doNothing().when(utilObj).geteratePDf();
 * doCallRealMethod().when(utilObj).createFile(anyString(), anyString());
 * doCallRealMethod().when(utilObj).createDirectory(anyString());
 * 
 * 
 * messageProcessor.processInputFile("D:\\PMU_Rewrite\\input\\mq2.processed");
 * 
 * verify(utilObj, times(1)).populateReportMetaData(anyString());
 * verify(utilObj, times(1)).replaceDynamicVariableInTemplate(repMetaDataObj);
 * verify(utilObj, times(1)).populateTransactionVariables(repMetaDataObj);
 * verify(utilObj, times(1)).replaceSpecialCharacterWithEmptySpace(anyString(),
 * anyString()); verify(utilObj,
 * times(1)).insertBarCodeToHtmlForTabularReport(anyString()); verify(utilObj,
 * times(1)).createFile(anyString(), anyString()); verify(utilObj,
 * times(1)).createDirectory(anyString());
 * 
 * assertTrue(new
 * File("C:\\PMURewrite\\JUnit\\output\\029\\CDSPRINTMAN.html").exists());
 * 
 * }
 * 
 * @Test void processInputFile_error_page_test() throws FileNotFoundException,
 * IOException {
 * 
 * when(utilObj.populateReportMetaData(anyString())).thenReturn(repMetaDataObj);
 * when(repMetaDataObj.getBranchId()).thenReturn(null);
 * when(propObj.getProperty("errorDirectoryOfTemplate")).thenReturn(
 * "C:\\PMURewrite\\JUnit\\error\\");
 * when(repMetaDataObj.getSourceFileContent()).thenReturn("Hello John Lewis");
 * doCallRealMethod().when(utilObj).createFile(anyString(), anyString());
 * doCallRealMethod().when(utilObj).createDirectory(anyString());
 * 
 * messageProcessor.processInputFile("D:\\PMU_Rewrite\\input\\mq2.processed");
 * 
 * verify(utilObj, times(1)).populateReportMetaData(anyString());
 * verify(utilObj, times(1)).createFile(anyString(), anyString());
 * verify(utilObj, times(1)).createDirectory(anyString());
 * 
 * assertTrue(new
 * File("C:\\PMURewrite\\JUnit\\error\\mq2.processed.err").exists());
 * 
 * } }
 */