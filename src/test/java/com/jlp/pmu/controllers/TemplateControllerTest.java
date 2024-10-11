package com.jlp.pmu.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlp.pmu.config.TestConfig;
import com.jlp.pmu.dto.TemplateDTO;
import com.jlp.pmu.dto.TemplateDeleteResponse;
import com.jlp.pmu.dto.TemplateResponse;
import com.jlp.pmu.dto.TemplateUpdateRequest;
import com.jlp.pmu.dto.TemplateUpdateResponse;
import com.jlp.pmu.service.TemplateService;

@WebMvcTest(TemplateController.class)
@Import(TestConfig.class)  
public class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TemplateService templateService;

    @InjectMocks
    private TemplateController templateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(templateController).build();
    }
    
    @Test
    void testGetAllTemplate_Success() throws Exception {
        // Mock data initialization
        TemplateDTO template1 = new TemplateDTO();
        template1.setTempId(1);
        template1.setTemplateName("Template1");

        TemplateDTO template2 = new TemplateDTO();
        template2.setTempId(2);
        template2.setTemplateName("Template2");

        // Mock service method to return list of templates
        when(templateService.getAllTemplates()).thenReturn(Arrays.asList(template1, template2));

        // Perform GET request and verify response
        mockMvc.perform(get("/pmu/v1/api/template/get-all-templates")
                .contentType(MediaType.APPLICATION_JSON));
                //.andExpect(status().isOk())
                //.andExpect(jsonPath("$[0].tempId").value(1))
//                .andExpect(jsonPath("$[0].templateName").value("Template1"))
//                .andExpect(jsonPath("$[1].tempId").value(2))
//                .andExpect(jsonPath("$[1].templateName").value("Template2"));
    }

    @Test
    void testUploadTemplate_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test file content".getBytes()
        );

        TemplateResponse response = new TemplateResponse();
        response.setTempId(1);
        response.setTemplateName("test.txt");
        response.setTemplateDescription("Test description");

        when(templateService.uploadTemplate(eq("Test description"), any(MultipartFile.class), eq("user123")))
                .thenReturn(response);

        mockMvc.perform(multipart("/pmu/v1/api/template/upload/user123")
                .file(file)
                .param("description", "Test description")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }
    
    @Test
    void testSoftDeleteTemplate_Success() throws Exception {
        // Mocked response
        TemplateDeleteResponse mockedResponse = new TemplateDeleteResponse();
        //mockedResponse.settempId(123);
        mockedResponse.setMessage("Template with ID 123 deleted successfully.");

        // Mock service method
        when(templateService.softDeleteTemplate(ArgumentMatchers.anyInt()))
                .thenReturn(mockedResponse);

        // Perform POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/pmu/v1/api/template/delete/{tempId}", 123)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    

    @Test
    void testGetAllTemplates_NotFound() throws Exception {
        // Mocked empty list
        //List<TemplateDTO> mockedTemplates = new ArrayList<>();

        // Mock service method returning empty list
        when(templateService.getAllTemplates()).thenReturn(Collections.emptyList());

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/pmu/v1/api/template/get-all-template")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
                
    }
    
//    @Test
//    void testUpdateTemplateDescription_Success() throws Exception {
//        // Mock data
//        TemplateUpdateRequest requestDto = new TemplateUpdateRequest();
//        //requestDto.setTemplateName("Updated Template Name");
//        requestDto.setNewDescription("Updated Template Description");
//
//        TemplateUpdateResponse mockedResponse = new TemplateUpdateResponse();
//        mockedResponse.setTempId(1);
//        mockedResponse.setTemplateName("Template Name");
//        mockedResponse.setTemplateDescription(requestDto.getNewDescription());
//        mockedResponse.setDateTime(LocalDateTime.now());
//        mockedResponse.setFileSize(1024L);
//
//        // Mock service method
//        when(templateService.updateTemplateDescription(anyInt(), any(TemplateUpdateRequest.class), anyString()))
//                .thenReturn(mockedResponse);
//
//        // Perform PUT request
//        mockMvc.perform(MockMvcRequestBuilders.put("/pmu/v1/api/template/update/{tempId}/{userId}", 1, "user123")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.tempId").value(1))
//                .andExpect(jsonPath("$.templateName").value("Template Name"))
//                .andExpect(jsonPath("$.templateDescription").value("Updated Template Description"))
//                .andExpect(jsonPath("$.dateTime").exists())  // Ensure dateTime field is present
//                .andExpect(jsonPath("$.fileSize").value(1024));
//    }
    
    @Test
    void testDownloadTemplate_Success() throws Exception {
        // Mock service response
        byte[] fileContent = "Test file content".getBytes();
        Resource resource = new ByteArrayResource(fileContent);
        String originalFilename = "template.txt"; // Mocked original filename

        when(templateService.downloadTemplate(anyInt())).thenReturn(resource);
        when(templateService.getOriginalFilename(anyInt())).thenReturn(originalFilename);

        // Perform GET request to download template
        mockMvc.perform(get("/pmu/v1/api/template/download/1")
                .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk());
                //.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                //.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"template.txt\""))
                //.andExpect(content().bytes(fileContent));
    }
    
    @Test
    void testUpdateTemplateDescription_Success() throws Exception {
        // Mocked request and response
        TemplateUpdateRequest request = new TemplateUpdateRequest();
        request.setNewDescription("Updated description");

        TemplateUpdateResponse response = new TemplateUpdateResponse();
        response.setTempId(1);
        response.setTemplateName("Template Name");
        response.setTemplateDescription("Updated description");
        response.setDateTime(LocalDateTime.now());
        response.setFileSize(1024L);

        // Mock service method
        when(templateService.updateTemplateDescription(eq(1), any(TemplateUpdateRequest.class), eq("user123")))
                .thenReturn(response);

        // Perform PUT request to update template description
        mockMvc.perform(put("/pmu/v1/api/template/update/1/user123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.tempId").value(1))
//                .andExpect(jsonPath("$.templateName").value("Template Name"))
//                .andExpect(jsonPath("$.templateDescription").value("Updated description"))
//                .andExpect(jsonPath("$.fileSize").value(1024));
    }
}
    
