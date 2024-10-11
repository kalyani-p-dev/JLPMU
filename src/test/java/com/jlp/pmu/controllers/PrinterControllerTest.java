package com.jlp.pmu.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlp.pmu.config.TestConfig;
import com.jlp.pmu.dto.PrinterRequest;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.RedirectionPrinterRequest.RedirectPrinterRequest;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.service.PrinterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers. *;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(PrinterController.class)
@Import(TestConfig.class)
public class PrinterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PrinterService printerService;

    @InjectMocks
    private PrinterController printerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(printerController).build();
    }

    @Test
    void testAddPrinterDetails_Success() throws Exception {
        // Mock data
    	PrinterRequest requestDto = PrinterRequest.builder()
    	        .userId("user123")
    	        .branchCode(123L)
    	        .printerID(456L)
    	        .printerName("Test Printer")
    	        .pmuPrinterName("PMU Printer")
    	        .printerPATH("/path/to/printer")
    	        .printerType(PrinterType.LASER)
    	        .comments("Test comments")
    	        .pmuServer("PMU Server")
    	        .localAttachedPrinter(true)
    	        .build();


        PrinterResponse mockedResponse = new PrinterResponse();
        mockedResponse.setPrinterID(456L);
        mockedResponse.setPrinterName("Test Printer");
        mockedResponse.setPmuPrinterName("PMU Printer");
        mockedResponse.setPrinterPATH("/path/to/printer");
        mockedResponse.setPrinterType(PrinterType.LASER);
        mockedResponse.setComments("Test comments");
        mockedResponse.setPmuServer("PMU Server");
        mockedResponse.setLocalAttachedPrinter(true);
     // Mock service method
        when(printerService.addPrinterDetails(ArgumentMatchers.any(PrinterRequest.class))).thenReturn(mockedResponse);

        // Perform POST request
        mockMvc.perform(post("/pmu/v1/api/printer/add-printer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }
    
    @Test
    void testUpdatePrinterDetails_Success() throws Exception {
        PrinterRequest requestDto = PrinterRequest.builder()
                .userId("user123")
                .branchCode(123L)
                .printerID(456L)
                .printerName("Test Printer")
                .pmuPrinterName("PMU Printer")
                .printerPATH("/path/to/printer")
                .printerType(PrinterType.LASER)
                .comments("Updated comments")
                .pmuServer("Updated PMU Server")
                .localAttachedPrinter(true)
                .build();

        PrinterResponse mockedResponse = new PrinterResponse();
        mockedResponse.setPrinterID(456L);
        mockedResponse.setPrinterName("Test Printer");
        mockedResponse.setPmuPrinterName("PMU Printer");
        mockedResponse.setPrinterPATH("/path/to/printer");
        mockedResponse.setPrinterType(PrinterType.LASER);
        mockedResponse.setComments("Updated comments");
        mockedResponse.setPmuServer("Updated PMU Server");
        mockedResponse.setLocalAttachedPrinter(true);

        when(printerService.updatePrinterDetails(ArgumentMatchers.any(PrinterRequest.class))).thenReturn(mockedResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/pmu/v1/api/printer/update-printer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
    
    @Test
    void testDeletePrinterDetailsByprinterID_Success() throws Exception {
        PrinterRequest requestDto = PrinterRequest.builder()
                .userId("user123")
                .printerID(456L)
                .build();

        PrinterResponse mockedResponse = new PrinterResponse();
        mockedResponse.setPrinterID(456L);
        // Set other fields as needed

        when(printerService.doInactivePrinterByprinterID(ArgumentMatchers.any(PrinterRequest.class))).thenReturn(mockedResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/pmu/v1/api/printer/inactive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
    
//    @Test
//    void testUpdateRedirectPrinter_Success() throws Exception {
//        RedirectPrinterRequest requestDto = new RedirectPrinterRequest();
//        requestDto.setUserId("user123");
//        // Set properties of requestDto as needed
//
//        PrinterResponse mockedResponse = new PrinterResponse();
//        // Set properties of mockedResponse as needed
//
//        when(printerService.updateRedirectPrinter(ArgumentMatchers.any(RedirectPrinterRequest.class))).thenReturn(mockedResponse);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/pmu/v1/api/printer/update-RedirectPrinter")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(requestDto)))
//                .andExpect(status().isOk());
//    }
    
//    @Test
//    void testGetListOfPrinterManagement_Success() throws Exception {
//        // Mock data
//        List<PrinterResponse> mockPrinterList = new ArrayList<>();
//        mockPrinterList.add(new PrinterResponse(/* Initialize with necessary data */));
//
//        // Mock service method call
//        when(printerService.getListOfPrinterManagement(ArgumentMatchers.anyLong())).thenReturn(mockPrinterList);
//
//        // Perform GET request
//        mockMvc.perform(MockMvcRequestBuilders.get("/pmu/v1/api/printer/get-ListOfPrinterManagement/branch/{branchCode}", 123L)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())  // Print the request and response
//                .andExpect(result -> {
//                    String json = result.getResponse().getContentAsString();
//                    // Use ObjectMapper to parse JSON string and validate the response
//                    // Example validation: Check if the JSON array length is as expected
//                    ObjectMapper mapper = new ObjectMapper();
//                    List<?> responseList = mapper.readValue(json, List.class);
//                    if (responseList.size() != mockPrinterList.size()) {
//                        throw new AssertionError("Expected size: " + mockPrinterList.size() + ", but got: " + responseList.size());
//                    }
//                });
//    }
    
//    @Test
//    void testRemoveRedirectionPrinter_Success() throws Exception {
//        // Mock data
//        RedirectPrinterRequest requestDto = new RedirectPrinterRequest();
//        requestDto.setPrinterID(123L);
//        requestDto.setRedirectPrintName("New Redirected Printer");
//
//        // Mocked response
//        PrinterResponse mockedResponse = new PrinterResponse();
//        mockedResponse.setPrinterID(123L);
//        mockedResponse.setRedirectPrintName("New Redirected Printer");
//
//        // Mock service method
//        when(printerService.removeRedirectionPrinter(ArgumentMatchers.any(RedirectPrinterRequest.class)))
//                .thenReturn(mockedResponse);
//
//        // Perform PUT request
//        mockMvc.perform(MockMvcRequestBuilders.put("/pmu/v1/api/printer/remove-RedirectionPrinter")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(requestDto)))
//                .andExpect(status().isOk());
//    }
    
    @Test
    void testGetListOfPrinterManagement_Success() throws Exception {
        // Mock data
        List<PrinterResponse> printerList = new ArrayList<>();

        PrinterResponse printer1 = new PrinterResponse();
        printer1.setPrinterID(1L);
        printer1.setPrinterName("Printer 1");
        printer1.setPmuPrinterName("PMU Printer 1");
        printer1.setPrinterPATH("/path/to/printer1");
        printer1.setPrinterType(PrinterType.LASER);
        printer1.setComments("Test comments 1");
        printer1.setPmuServer("PMU Server 1");
        printer1.setLocalAttachedPrinter(true);
        printer1.setRedirectPrintName("Redirect Printer 1");

        PrinterResponse printer2 = new PrinterResponse();
        printer2.setPrinterID(2L);
        printer2.setPrinterName("Printer 2");
        printer2.setPmuPrinterName("PMU Printer 2");
        printer2.setPrinterPATH("/path/to/printer2");
        printer2.setPrinterType(PrinterType.LASER);
        printer2.setComments("Test comments 2");
        printer2.setPmuServer("PMU Server 2");
        printer2.setLocalAttachedPrinter(false);
        printer2.setRedirectPrintName("Redirect Printer 2");



        printerList.add(printer1);
        printerList.add(printer2);

        // Mock service method
        when(printerService.getListOfPrinterManagement(ArgumentMatchers.anyLong())).thenReturn(printerList);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/pmu/v1/api/printer/get-ListOfPrinterManagement/branch/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.length()").value(printerList.size()))  // Check if response contains all printers
//                .andExpect(jsonPath("$[0].printerID").value(1L))  // Example assertion, adjust as per your response structure
//                .andExpect(jsonPath("$[0].printerName").value("Printer 1"))
//                .andExpect(jsonPath("$[1].printerID").value(2L))  // Example assertion, adjust as per your response structure
//                .andExpect(jsonPath("$[1].printerName").value("Printer 2"));

        // Add more assertions as needed for other fields or objects in the response
    }
    
//    @Test
//    void testUpdateRedirectPrinter_Success() throws Exception {
//        // Mock data
//        RedirectPrinterRequest requestDto = new RedirectPrinterRequest();
//        requestDto.setUserId("user123");
//        requestDto.setBranchCode(123L);
//        requestDto.setPrinterName("Test Printer");
//        requestDto.setRedirectPrintName("Updated Redirect Printer");
//
//
//
//        PrinterResponse mockedResponse = new PrinterResponse();
//        mockedResponse.setPrinterID(456L);
//        mockedResponse.setPrinterName("Test Printer");
//        mockedResponse.setPmuPrinterName("PMU Printer");
//        mockedResponse.setPrinterPATH("/path/to/printer");
//        mockedResponse.setPrinterType(PrinterType.LASER);
//        mockedResponse.setComments("Updated comments");
//        mockedResponse.setPmuServer("Updated PMU Server");
//        mockedResponse.setLocalAttachedPrinter(true);
//        mockedResponse.setRedirectPrintName("Updated Redirect Printer");
//
//        // Mock service method
//        when(printerService.updateRedirectPrinter(ArgumentMatchers.any(RedirectPrinterRequest.class))).thenReturn(mockedResponse);
//
//        // Perform PUT request
//        mockMvc.perform(MockMvcRequestBuilders.put("/pmu/v1/api/printer/update-RedirectPrinter")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.printerID").value(456L))
//                .andExpect(jsonPath("$.printerName").value("Test Printer"))
//                .andExpect(jsonPath("$.pmuPrinterName").value("PMU Printer"))
//                .andExpect(jsonPath("$.printerPATH").value("/path/to/printer"))
//                .andExpect(jsonPath("$.printerType").value("LASER"))
//                .andExpect(jsonPath("$.comments").value("Updated comments"))
//                .andExpect(jsonPath("$.pmuServer").value("Updated PMU Server"))
//                .andExpect(jsonPath("$.localAttachedPrinter").value(true))
//                .andExpect(jsonPath("$.redirectPrintName").value("Updated Redirect Printer"));
//    }
}



    
    





    
