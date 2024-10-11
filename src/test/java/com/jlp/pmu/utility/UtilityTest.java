package com.jlp.pmu.utility;
/*
 * package com.jlp.pmu.utility;
 * 
 * import static org.junit.Assert.assertFalse; import static
 * org.junit.Assert.assertNotNull; import static org.junit.Assert.assertNull;
 * import static org.junit.Assert.assertTrue; import static
 * org.junit.jupiter.api.Assertions.assertEquals; import static
 * org.junit.jupiter.api.Assertions.assertNotEquals; import static
 * org.mockito.ArgumentMatchers.anyString; import static
 * org.mockito.Mockito.doNothing; import static org.mockito.Mockito.doReturn;
 * import static org.mockito.Mockito.mockStatic; import static
 * org.mockito.Mockito.when;
 * 
 * import java.io.File; import java.io.FileNotFoundException; import
 * java.io.IOException; import java.lang.reflect.InvocationTargetException;
 * import java.lang.reflect.Method; import java.util.ArrayList; import
 * java.util.Arrays; import java.util.HashMap; import java.util.List; import
 * java.util.Map; import java.util.Properties; import java.util.Scanner;
 * 
 * import org.apache.commons.lang3.StringUtils; import
 * org.junit.jupiter.api.Test; import
 * org.junit.jupiter.api.extension.ExtendWith; import org.mockito.InjectMocks;
 * import org.mockito.Mock; import org.mockito.MockedStatic; import
 * org.mockito.junit.jupiter.MockitoExtension; import
 * org.springframework.boot.test.context.SpringBootTest;
 * 
 * import com.jlp.pmu.pojo.ReportMetaData; import com.jlp.pmu.utility.Utility;
 * 
 * @SpringBootTest
 * 
 * @ExtendWith(MockitoExtension.class) class UtilityTest {
 * 
 * static String TEST_INPUT_FILE_PATH =
 * "C:\\PMURewrite\\JUnit\\input\\test.processed";
 * 
 * static String MQ_TABULAR_TEST_FILE_PATH =
 * "C:\\PMURewrite\\JUnit\\input\\mq2_test.processed";
 * 
 * static String MQ_NORMAL_TEST_FILE_PATH =
 * "C:\\PMURewrite\\JUnit\\input\\mq1_test.processed";
 * 
 * static String CBS_NORMAL_TEST_FILE_PATH =
 * "C:\\PMURewrite\\JUnit\\input\\cbs_test";
 * 
 * static String TEST_OUTPUT_FILE_PATH =
 * "C:\\PMURewrite\\JUnit\\output\\test_output.html";
 * 
 * 
 * 
 * 
 * public static Properties prop=null;
 * 
 * @Mock public ReportMetaData repMetaData;
 * 
 * @InjectMocks Utility utilObj = new Utility();
 * 
 * 
 * 
 * //createHeaderFormatForCBSReport
 * 
 * @Test void createHeaderFormatForCBSReport_test() throws
 * IllegalAccessException, IllegalArgumentException, InvocationTargetException,
 * NoSuchMethodException, SecurityException { String input =
 * "VDLHDR706000 VER=001 CPL=133";
 * 
 * Method method =
 * Utility.class.getDeclaredMethod("createHeaderFormatForCBSReport",
 * String.class); method.setAccessible(true);
 * 
 * String output = (String) method.invoke(utilObj, input);
 * 
 * assertEquals("VDLHDR/706/", output); }
 * 
 * // isLineContainsString
 * 
 * @Test void isLineContainsString_test() {
 * 
 * String input = "SSMCDPLABELS2e /045/042e /h /? /h /c NNe"; String
 * exclusionList = "SSMCDPLABELS2e,/c NNe /";
 * 
 * boolean found = utilObj.isLineContainsString(input, exclusionList);
 * 
 * assertTrue(found); }
 * 
 * @Test void isLineContainsString_no_exclusionList_arguments_test() {
 * 
 * String input = "SSMCDPLABELS2e /045/042e /h /? /h /c NNe"; String
 * exclusionList = "";
 * 
 * boolean found = utilObj.isLineContainsString(input, exclusionList);
 * 
 * assertFalse(found); }
 * 
 * 
 * //processNormalReport
 * 
 * @Test void processNormalReport_test() { List<String> inputList =
 * Arrays.asList("","BATESON","86562728","CDSPICKUP");
 * 
 * when(repMetaData.getListOfEachLinesInFile()).thenReturn(inputList);
 * 
 * String output = utilObj.processNormalReport(repMetaData);
 * 
 * assertEquals("BATESON<br></br>86562728<br></br>CDSPICKUP<br></br>", output);
 * }
 * 
 * //createFile
 * 
 * @Test void createFile_test() throws FileNotFoundException {
 * 
 * String content = "Hello John Lewis";
 * 
 * utilObj.createFile(TEST_OUTPUT_FILE_PATH, content);
 * 
 * //asserting output
 * 
 * Scanner scanner = new Scanner(new File(TEST_OUTPUT_FILE_PATH));
 * 
 * assertTrue(scanner.hasNextLine());
 * 
 * assertEquals(content, scanner.nextLine());
 * 
 * }
 * 
 * 
 * //geteratePDf
 * 
 * @Test void geteratePDf_normal_test() throws FileNotFoundException,
 * IOException {
 * 
 * prop = mockedProperties(); utilObj.setProperties(prop);
 * 
 * //mocking
 * 
 * when(repMetaData.getFinalHtmlContent()).
 * thenReturn("<h1>Hello John Lewis & Waitrose</h1>");
 * when(repMetaData.getReportName()).thenReturn("CDSPRINTMAN");
 * when(repMetaData.getBranchId()).thenReturn("029");
 * 
 * 
 * utilObj.geteratePDf();
 * 
 * File file = new File(prop.getProperty("SourceDirectoryOfPDF") +
 * "029//CDSPRINTMAN.pdf");
 * 
 * assertTrue(file.exists());
 * 
 * }
 * 
 * 
 * 
 * //replaceSpecialCharacterWithEmptySpace
 * 
 * 
 * @Test void replaceSpecialCharacterWithEmptySpace_test() { String input =
 * "CDSPRTMAN01CDSPRTMAN02"; String exclusionCSV = "";
 * 
 * String output = utilObj.replaceSpecialCharacterWithEmptySpace(input,
 * exclusionCSV);
 * 
 * assertEquals("CDSPRTMAN01CDSPRTMAN02", output); }
 * 
 * //replaceDynamicVariableInTemplate
 * 
 * @Test void replaceDynamicVariableInTemplate_test() {
 * 
 * ReportMetaData reData = new ReportMetaData();
 * 
 * String inputValue =
 * "R43AM20230922T1<br></br>FileNotFoundException<br></br>inputList<br></br>";
 * String inputKey = "$mqContent";
 * 
 * Map<String,String> inputMap = new HashMap<>();
 * 
 * String inputHtmlContent = "throws $mqContent HashMap";
 * 
 * 
 * reData.setHtmlFileContentWithParameter(inputHtmlContent);
 * reData.setKeyValueOfTemplate(inputKey, inputValue);
 * 
 * Utility utility = new Utility();
 * 
 * String output = utility.replaceDynamicVariableInTemplate(reData);
 * 
 * assertNotNull(output);
 * 
 * String expected =
 * "throws R43AM20230922T1<br></br>FileNotFoundException<br></br>inputList<br></br> HashMap"
 * ;
 * 
 * assertEquals(expected, output);
 * 
 * }
 * 
 * //generateTabularReport
 * 
 * @Test void generateTabularReport_test() throws FileNotFoundException {
 * 
 * List inputList = input_for_generateTabularReport_test();
 * 
 * //mocking
 * 
 * when(repMetaData.getListOfEachLinesInFile()).thenReturn(inputList);
 * 
 * //actual
 * 
 * String output = utilObj.generateTabularReport(repMetaData);
 * 
 * assertNotEquals("", output);
 * 
 * String[] outputArray = output.split("<br></br>");
 * 
 * assertEquals(inputList.size() - 10 , Arrays.asList(outputArray).size());
 * 
 * 
 * 
 * }
 * 
 * List input_for_generateTabularReport_test() throws FileNotFoundException {
 * 
 * File file = new File(MQ_TABULAR_TEST_FILE_PATH); String inputString = "";
 * 
 * List inputList = new ArrayList<>();
 * 
 * Scanner scanner = new Scanner(file);
 * 
 * while(scanner.hasNext()) {
 * 
 * inputString = inputString + (scanner.nextLine() + "\n"); }
 * 
 * String[] inputArray = inputString.split("\n");
 * 
 * inputList.add(Arrays.asList(inputArray));
 * 
 * return (List) inputList.get(0); }
 * 
 * 
 * // generateTabularReport_new
 * 
 * @Test void generateTabularReport_new_test() throws FileNotFoundException {
 * 
 * String[] inputList = {"CDSPRTMAN02 GHF34 \n"
 * ,"CDSPRTMAN02 RouteID \n CDSPRTMAN02 Route Manifest THEALE \n"};
 * 
 * //mocking
 * 
 * when(repMetaData.getListOfUniqueRecordsInSourceFile()).thenReturn(Arrays.
 * asList(inputList));
 * 
 * //actual
 * 
 * String output = utilObj.generateTabularReport_new(repMetaData);
 * 
 * assertEquals("<div class=\"page-break\"></div> RouteID <br></br> Route Manifest THEALE <br></br><div class=\"page-break\"></div>"
 * , output);
 * 
 * }
 * 
 * //getTheListOfAllWordsInLine
 * 
 * @Test void getTheListOfAllWordsInLine_test() {
 * 
 * String input = "<html lang=\"en\">";
 * 
 * List<String> list = utilObj.getTheListOfAllWordsInLine(input);
 * 
 * assertEquals(Arrays.asList("<html", "lang", "en", ">" ), list);
 * 
 * }
 * 
 * 
 * 
 * //getIndexOfGivenWordInLineList
 * 
 * @Test void getIndexOfGivenWordInLineList_test() throws
 * IllegalAccessException, IllegalArgumentException, InvocationTargetException,
 * NoSuchMethodException, SecurityException {
 * 
 * String input = "<html lang=\"en\">"; String stringToBELocated = "en";
 * 
 * Method method =
 * Utility.class.getDeclaredMethod("getIndexOfGivenWordInLineList",
 * String.class, String.class); method.setAccessible(true);
 * 
 * int index = (int) method.invoke(utilObj, input, stringToBELocated);
 * 
 * assertEquals(2, index);
 * 
 * }
 * 
 * //insertBarCodeToHtmlForTabularReport
 * 
 * @Test void insertBarCodeToHtmlForTabularReport_for_mq_tabular_test() {
 * 
 * String inputHtmlContent =
 * "prop\n Utility RouteID: R43AM20230922T1 Daytime Evening routeID: R43AM20230922T2 CDSPRTMAN02\n"
 * ; String barCodeIdentifier = "RouteID:";
 * 
 * prop = mockedProperties();
 * 
 * 
 * 
 * //mocking
 * when(repMetaData.getFinalHtmlContent()).thenReturn(inputHtmlContent);
 * when(repMetaData.getReportType()).thenReturn("MQ_Tabular");
 * 
 * utilObj.insertBarCodeToHtmlForTabularReport(barCodeIdentifier);
 * 
 * String barcodeFilePath = prop.getProperty("BarCodeRootFolderAbsPath") +
 * "R43AM20230922T1.gif"; String barcodeFilePath2 =
 * prop.getProperty("BarCodeRootFolderAbsPath") + "R43AM20230922T2.gif";
 * 
 * assertTrue(new File(barcodeFilePath).exists()); assertTrue(new
 * File(barcodeFilePath2).exists());
 * 
 * }
 * 
 * @Test void insertBarCodeToHtmlForTabularReport_for_mq_normal_test() {
 * 
 * String inputHtmlContent =
 * "prop\n Utility 63609555301¢ Daytime Evening routeID: 63609158901¢ CDSPRTMAN02\n"
 * ; String barCodeIdentifier = "Â¢";
 * 
 * prop = mockedProperties();
 * 
 * 
 * 
 * //mocking
 * when(repMetaData.getFinalHtmlContent()).thenReturn(inputHtmlContent);
 * when(repMetaData.getReportType()).thenReturn("MQ_Normal");
 * 
 * utilObj.insertBarCodeToHtmlForTabularReport(barCodeIdentifier);
 * 
 * String barcodeFilePath = prop.getProperty("BarCodeRootFolderAbsPath") +
 * "63609555301.gif"; String barcodeFilePath2 =
 * prop.getProperty("BarCodeRootFolderAbsPath") + "63609158901.gif";
 * 
 * assertTrue(new File(barcodeFilePath).exists()); assertTrue(new
 * File(barcodeFilePath2).exists());
 * 
 * }
 * 
 * //createBarCode
 * 
 * @Test void createBarCode_test() { String barCodeValue = "R43AM20230922V4";
 * 
 * prop = mockedProperties();
 * 
 * utilObj.setProperties(prop);
 * 
 * utilObj.createBarCode(barCodeValue);
 * 
 * String barcodeFilePath = prop.getProperty("BarCodeRootFolderAbsPath") +
 * barCodeValue + ".gif";
 * 
 * assertTrue(new File(barcodeFilePath).exists());
 * 
 * 
 * }
 * 
 * 
 * //getTheUniqueBarCodeIdentifier
 * 
 * @Test void getTheUniqueBarCodeIdentifier_for_mq_tabular_test() throws
 * NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
 * InvocationTargetException {
 * 
 * //inputs String[] inputArray =
 * {"inputArray","utilObj","RouteID:","R43AM20230922T1","static","String",
 * "processed","RouteID:","","output","ArrayList","lineContent","assertEquals",
 * "@Test","routeID:","R43AM20230922T2"}; List inputList =
 * Arrays.asList(inputArray);
 * 
 * //mocking when(repMetaData.getReportType()).thenReturn("MQ_Tabular");
 * 
 * // making the private method to accessible Method method =
 * Utility.class.getDeclaredMethod("getTheUniqueBarCodeIdentifier", List.class,
 * String.class); method.setAccessible(true);
 * 
 * //invoking method List output = (List) method.invoke(utilObj,
 * inputList,"RouteID:");
 * 
 * //assertions String[] expectedArray = {"R43AM20230922T1", "R43AM20230922T2"};
 * List expected = Arrays.asList(expectedArray);
 * 
 * assertEquals(expected, output);
 * 
 * }
 * 
 * @Test void getTheUniqueBarCodeIdentifier_for_mq_normal_test() throws
 * NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
 * InvocationTargetException {
 * 
 * //inputs String[] inputArray =
 * {"inputArray","utilObj","R43AM20230922T1","static","String","processed",
 * "RouteID:","63609158901¢","output","ArrayList","lineContent","assertEquals",
 * "@Test","63609555301¢"}; List inputList = Arrays.asList(inputArray);
 * 
 * //mocking when(repMetaData.getReportType()).thenReturn("MQ_Normal");
 * 
 * // making the private method to accessible Method method =
 * Utility.class.getDeclaredMethod("getTheUniqueBarCodeIdentifier", List.class,
 * String.class); method.setAccessible(true);
 * 
 * //invoking method List output = (List) method.invoke(utilObj, inputList,"¢");
 * 
 * //assertions String[] expectedArray = {"63609158901", "63609555301"}; List
 * expected = Arrays.asList(expectedArray);
 * 
 * assertEquals(expected, output);
 * 
 * }
 * 
 * 
 * 
 * 
 * //getTheListOfAllWordsInLineWithSpaceAsDelimiter
 * 
 * @Test void getTheListOfAllWordsInLineWithSpaceAsDelimiter_normal_test() {
 * String lineContent = "<html lang=\"en\">";
 * 
 * List output =
 * utilObj.getTheListOfAllWordsInLineWithSpaceAsDelimiter(lineContent);
 * 
 * List expected = new ArrayList<>(); expected.add("<html");
 * expected.add("lang=\"en\">");
 * 
 * assertEquals(expected, output);
 * 
 * }
 * 
 * 
 * //getTheExactTransactionValueFromSourceFile
 * 
 * @Test void getTheExactTransactionValueFromSourceFile_mq_tabular_test() throws
 * FileNotFoundException, NoSuchMethodException, SecurityException,
 * IllegalAccessException, IllegalArgumentException, InvocationTargetException {
 * 
 * List inputList =
 * input_for_getTheExactTransactionValueFromSourceFile_mq_tabular_test();
 * 
 * //mocking
 * 
 * when(repMetaData.getReportType()).thenReturn("MQ_Tabular");
 * when(repMetaData.getListOfUniqueRecordsInSourceFile()).thenReturn(inputList);
 * 
 * //actual Method method =
 * Utility.class.getDeclaredMethod("getTheExactTransactionValueFromSourceFile",
 * int.class, int.class, int.class); method.setAccessible(true); String output =
 * (String) method.invoke(utilObj, 3, 21, 50);
 * 
 * //assertion assertTrue(StringUtils.contains(output, "Graeme Walton"));
 * 
 * 
 * 
 * }
 * 
 * List input_for_getTheExactTransactionValueFromSourceFile_mq_tabular_test()
 * throws FileNotFoundException {
 * 
 * File file = new File(MQ_TABULAR_TEST_FILE_PATH); String inputString = "";
 * 
 * List inputList = new ArrayList<>();
 * 
 * Scanner scanner = new Scanner(file);
 * 
 * while(scanner.hasNext()) {
 * 
 * inputString = inputString + (scanner.nextLine() + "\n"); }
 * 
 * String[] inputArray = inputString.split("");
 * 
 * inputList.add(Arrays.asList(inputArray));
 * 
 * return (List) inputList.get(0); }
 * 
 * @Test void getTheExactTransactionValueFromSourceFile_mq_normal_test() throws
 * FileNotFoundException, NoSuchMethodException, SecurityException,
 * IllegalAccessException, IllegalArgumentException, InvocationTargetException {
 * 
 * List inputList =
 * input_for_getTheExactTransactionValueFromSourceFile_mq_normal_test();
 * 
 * //mocking
 * 
 * when(repMetaData.getReportType()).thenReturn("MQ_Normal");
 * when(repMetaData.getListOfUniqueRecordsInSourceFile()).thenReturn(inputList);
 * 
 * //actual Method method =
 * Utility.class.getDeclaredMethod("getTheExactTransactionValueFromSourceFile",
 * int.class, int.class, int.class); method.setAccessible(true); String output =
 * (String) method.invoke(utilObj, 1, 1, 5);
 * 
 * //assertion assertTrue(StringUtils.contains(output, "Mrs"));
 * 
 * }
 * 
 * List input_for_getTheExactTransactionValueFromSourceFile_mq_normal_test()
 * throws FileNotFoundException {
 * 
 * File file = new File(MQ_NORMAL_TEST_FILE_PATH); String inputString = "";
 * 
 * List inputList = new ArrayList<>();
 * 
 * Scanner scanner = new Scanner(file);
 * 
 * while(scanner.hasNext()) {
 * 
 * inputString = inputString + (scanner.nextLine() + "\n"); }
 * 
 * String[] inputArray = inputString.split("");
 * 
 * inputList.add(Arrays.asList(inputArray));
 * 
 * return (List) inputList.get(0); }
 * 
 * 
 * @Test void getTheExactTransactionValueFromSourceFile_cbs_tabular_test()
 * throws FileNotFoundException, NoSuchMethodException, SecurityException,
 * IllegalAccessException, IllegalArgumentException, InvocationTargetException {
 * 
 * List inputList =
 * input_for_getTheExactTransactionValueFromSourceFile_mq_normal_test();
 * 
 * //mocking
 * 
 * when(repMetaData.getReportType()).thenReturn("MQ_Normal");
 * when(repMetaData.getListOfUniqueRecordsInSourceFile()).thenReturn(inputList);
 * 
 * //actual Method method =
 * Utility.class.getDeclaredMethod("getTheExactTransactionValueFromSourceFile",
 * int.class, int.class, int.class); method.setAccessible(true); String output =
 * (String) method.invoke(utilObj, 1, 1, 5);
 * 
 * //assertion assertTrue(StringUtils.contains(output, "Mrs"));
 * 
 * }
 * 
 * //populateTransactionVariables
 * 
 * @Test public void populateTransactionVariables_test() throws Exception {
 * 
 * utilObj.setProperties(mockedProperties());
 * 
 * //mocking
 * 
 * when(repMetaData.getKeyPrefix()).thenReturn("MQ_");
 * when(repMetaData.getReportName()).thenReturn("CDSPRINTMAN");
 * 
 * utilObj.populateTransactionVariables(repMetaData);
 * 
 * 
 * }
 * 
 * //populateReportMetaData
 * 
 * @Test void populateReportMetaData_mq_test() throws FileNotFoundException {
 * 
 * String asciiFilePath = MQ_TABULAR_TEST_FILE_PATH;
 * 
 * 
 * prop = mockedProperties();
 * 
 * utilObj.setProperties(prop);
 * 
 * ReportMetaData outputPojo = utilObj.populateReportMetaData(asciiFilePath);
 * 
 * 
 * // assertions
 * 
 * 
 * assertNotNull(outputPojo);
 * 
 * assertEquals("CDSPRINTMAN", outputPojo.getReportName());
 * 
 * assertEquals("029", outputPojo.getBranchId());
 * 
 * assertEquals(208 , outputPojo.getListOfEachLinesInFile().size());
 * 
 * 
 * assertTrue(outputPojo.getListOfAllWordsInTemplateFile().
 * contains("       $mqContent")); }
 * 
 * @Test void populateReportMetaData_cbs_test() throws FileNotFoundException {
 * 
 * String asciiFilePath = CBS_NORMAL_TEST_FILE_PATH;
 * 
 * 
 * prop = mockedProperties();
 * 
 * utilObj.setProperties(prop);
 * 
 * ReportMetaData outputPojo = utilObj.populateReportMetaData(asciiFilePath);
 * 
 * 
 * // assertions
 * 
 * 
 * assertNotNull(outputPojo);
 * 
 * assertEquals("VDLHDR", outputPojo.getReportName());
 * 
 * assertEquals("045", outputPojo.getBranchId());
 * 
 * assertTrue(outputPojo.getListOfAllWordsInTemplateFile().contains(
 * "$cbsContent")); }
 * 
 * 
 * 
 * 
 * //fileContentAsLineList
 * 
 * @Test void fileContentAsLineList_normal_test() { String fileContent =
 * "John\nLewis\nPartnership\nWaitrose\n"; List output =
 * utilObj.fileContentAsLineList(fileContent);
 * 
 * List expected = new ArrayList<>(); String[] list =
 * {"John","Lewis","Partnership","Waitrose"};
 * expected.addAll(Arrays.asList(list));
 * 
 * assertEquals(expected, output); }
 * 
 * 
 * //readEntireFileIntoString
 * 
 * @Test void readEntireFileIntoString_normal_test() throws
 * IllegalAccessException, IllegalArgumentException, InvocationTargetException,
 * NoSuchMethodException, SecurityException { String filePath =
 * TEST_INPUT_FILE_PATH;
 * 
 * Method method = Utility.class.getDeclaredMethod("readEntireFileIntoString",
 * String.class); method.setAccessible(true);
 * 
 * String outputContent = (String) method.invoke(utilObj, filePath);
 * 
 * assertEquals("John\nLewis\nPartnership\nWaitrose\n", outputContent);
 * 
 * }
 * 
 * @Test void readEntireFileIntoString_exception_test() throws
 * NoSuchMethodException, SecurityException, IllegalAccessException,
 * IllegalArgumentException, InvocationTargetException { String filePath =
 * TEST_INPUT_FILE_PATH + "hi";
 * 
 * Method method = Utility.class.getDeclaredMethod("readEntireFileIntoString",
 * String.class); method.setAccessible(true);
 * 
 * String outputContent = (String) method.invoke(utilObj, filePath);
 * 
 * assertEquals("", outputContent);
 * 
 * }
 * 
 * 
 * 
 * 
 * 
 * //readFileContent
 * 
 * @Test void readFileContent_normal_test() {
 * 
 * 
 * 
 * String filePath = TEST_INPUT_FILE_PATH;
 * 
 * String outputContent = utilObj.readFileContent(filePath);
 * 
 * assertTrue(StringUtils.contains(outputContent, "John"));
 * assertTrue(StringUtils.contains(outputContent, "Waitrose"));
 * 
 * 
 * }
 * 
 * @Test void readFileContent_exception_test() {
 * 
 * 
 * String filePath = TEST_INPUT_FILE_PATH + "hi";
 * 
 * String outputContent = utilObj.readFileContent(filePath);
 * 
 * assertNull(outputContent);
 * 
 * 
 * }
 * 
 * 
 * 
 * //readInputFileContent_normal_test
 * 
 * @Test void readInputFileContent_normal_test() throws IllegalAccessException,
 * IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
 * SecurityException {
 * 
 * String filePath = TEST_INPUT_FILE_PATH;
 * 
 * Method method = Utility.class.getDeclaredMethod("readInputFileContent",
 * String.class); method.setAccessible(true);
 * 
 * String outputContent = (String) method.invoke(utilObj, filePath);
 * 
 * assertEquals("JohnLewisPartnershipWaitrose", outputContent);
 * 
 * }
 * 
 * @Test void readInputFileContent_exception_test() throws
 * IllegalAccessException, IllegalArgumentException, InvocationTargetException,
 * NoSuchMethodException, SecurityException {
 * 
 * String filePath = TEST_INPUT_FILE_PATH + "\\hello";
 * 
 * Method method = Utility.class.getDeclaredMethod("readInputFileContent",
 * String.class); method.setAccessible(true);
 * 
 * String outputContent = (String) method.invoke(utilObj, filePath);
 * 
 * assertEquals("", outputContent);
 * 
 * }
 * 
 * @Test void readPropertiesFile_normal_test() throws IOException {
 * 
 * Properties properties = Utility.readPropertiesFile();
 * 
 * assertNotNull(properties);
 * 
 * assertEquals("C:\\projects\\PMU\\templates\\", properties.getProperty("
 * SourceDirectoryOfTemplate")); assertEquals("12,25,50",
 * properties.getProperty("MQ_CDSPRINTMAN_transactionVariable_Phone_Number"));
 * assertEquals("mq_CDSPRINTMAN.html",
 * properties.getProperty("MQ_CDSPRINTMAN"));
 * assertEquals("C:\\projects\\PMU\\barcode\\", properties.getProperty("
 * BarCodeRootFolderAbsPath"));
 * 
 * }
 * 
 * 
 * //Common members
 * 
 * Properties mockedProperties() {
 * 
 * Properties props = new Properties(); props.put("SourceDirectoryOfTemplate",
 * "C:\\PMURewrite\\JUnit\\input\\"); props.put("MQ_CDSPRINTMAN",
 * "mq_CDSPRINTMAN.html"); props.put("MQ_CDSPRINTMAN_transactionVariables",
 * "Customer_Name,Phone_Number,Delivery_Time");
 * props.put("MQ_CDSPRINTMAN_transactionVariable_Customer_Name", "3,21,50");
 * props.put("MQ_CDSPRINTMAN_transactionVariable_Phone_Number", "12,25,50");
 * props.put("BarCodeRootFolderAbsPath", "C:\\PMURewrite\\JUnit\\barcode\\");
 * props.put("SourceDirectoryOfPDF", "C:\\PMURewrite\\JUnit\\pdf\\");
 * props.put("CBS_VDLHDR", "cbs_SSMCDPLABELS2e.html");
 * 
 * 
 * 
 * return props; }
 * 
 * }
 */

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jlp.pmu.pojo.ReportMetaData;

public class UtilityTest {

    private Utility utility;

    @Mock
    private ReportMetaData repMetaData;

    @Mock
    private Properties prop;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File customTempDir;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        utility = new Utility();
        utility.repMetaData = repMetaData;
        utility.prop = prop;

        try {
            // Initialize customTempDir using TemporaryFolder rule
            customTempDir = folder.newFolder("pdf_file");
        } catch (IOException e) {
            // Handle initialization error
            e.printStackTrace();
            // You may choose to fail the test or handle the error gracefully
            fail("Failed to initialize customTempDir");


        }
    }

    @Test
    public void testHtmlToPdf() throws Exception {
        // Mock properties
        String applicationDataRootDir = customTempDir.getAbsolutePath(); 
        String branchName = "testBranch";
        String htmlOutputDirectory = "htmlOutput";

        when(prop.getProperty("applicationDataRootDir")).thenReturn(applicationDataRootDir);
        when(prop.getProperty("BranchName_" + repMetaData.getBranchId())).thenReturn(branchName);
        when(prop.getProperty("htmlOutputDirectory")).thenReturn(htmlOutputDirectory);

        // Mock HTML content
        String htmlContent = "<html><body><h1>Test</h1></body></html>";
        when(repMetaData.getFinalHtmlContent()).thenReturn(htmlContent);

        // Define output PDF file path within the custom temporary directory
        File pdfFile = new File(customTempDir, "output.pdf");
        String absPathOfPdfFile = pdfFile.getAbsolutePath();

        // Call the method
        utility.htmlToPdf(absPathOfPdfFile);

        // Verify that the PDF file is created and has content
        assertTrue("PDF file should be created", pdfFile.exists());
        assertTrue("PDF file should not be empty", pdfFile.length() > 0);
    }
}
