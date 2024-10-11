package com.jlp.pmu.service.impl;

import com.jlp.pmu.constant.Constant;
import com.jlp.pmu.dto.TemplateDTO;
import com.jlp.pmu.dto.TemplateDeleteResponse;
import com.jlp.pmu.dto.TemplateResponse;
import com.jlp.pmu.dto.TemplateUpdateRequest;
import com.jlp.pmu.dto.TemplateUpdateResponse;
import com.jlp.pmu.enums.ObjectType;
import com.jlp.pmu.exception.TemplateAlreadyExistException;
import com.jlp.pmu.exception.TemplateNotFoundException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Template;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.TemplateRepository;
import com.jlp.pmu.repository.UserRepository;
import com.jlp.pmu.utility.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceImplTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Utility utility;

    @InjectMocks
    @Spy
    private TemplateServiceImpl templateServiceImpl;
    
    //@Spy
    //private TemplateServiceImpl templateServiceImplSpy;

    private User mockedUser;
    private Template mockedTemplate;
    private Template mockedTemplate1;
    private Template mockedTemplate2;
    private Activity mockedActivity;
    private MultipartFile mockedFile;
    private Properties mockedProperties=new Properties();
    private static final String USER_ID = "A12345";
    private static final int TEMPLATE_ID = 1;

    @BeforeEach
    void setUp() {
        mockedUser = new User();
        mockedUser.setUserId(USER_ID);
        mockedUser.setFirstName("Alice");
        mockedUser.setLastName("Smith");

        mockedTemplate = new Template();
        mockedTemplate.setTempId(TEMPLATE_ID);
        mockedTemplate.setTemplateName("template.html");
        mockedTemplate.setTemplateDescription("Test Template");
        mockedTemplate.setFileSIze(12345L);
        mockedTemplate.setDateTime(LocalDateTime.now());
        mockedTemplate.setLastupdatedtime(LocalDateTime.now());
        mockedTemplate.setStatus(true);
        mockedTemplate.setCreatedBy("Alice Smith");
        mockedTemplate.setUpdatedBy("Alice Smith");
        mockedTemplate.setComment("added By Alice Smith");
        mockedTemplate1 = new Template();
        mockedTemplate1.setTempId(1);
        mockedTemplate1.setTemplateName("template1.html");
        mockedTemplate1.setTemplateDescription("Template 1");
        mockedTemplate1.setFileSIze(12345L);
        mockedTemplate1.setDateTime(LocalDateTime.now());
        mockedTemplate1.setLastupdatedtime(LocalDateTime.now());
        mockedTemplate1.setStatus(true);
        mockedTemplate1.setCreatedBy("Alice Smith");
        mockedTemplate1.setUpdatedBy("Alice Smith");
        mockedTemplate1.setComment("Added by Alice Smith");

        mockedTemplate2 = new Template();
        mockedTemplate2.setTempId(2);
        mockedTemplate2.setTemplateName("template2.html");
        mockedTemplate2.setTemplateDescription("Template 2");
        mockedTemplate2.setFileSIze(23456L);
        mockedTemplate2.setDateTime(LocalDateTime.now());
        mockedTemplate2.setLastupdatedtime(LocalDateTime.now());
        mockedTemplate2.setStatus(true);
        mockedTemplate2.setCreatedBy("Alice Smith");
        mockedTemplate2.setUpdatedBy("Alice Smith");
        mockedTemplate2.setComment("Added by Alice Smith");

        mockedActivity = new Activity();
        mockedActivity.setCreatedBy("Alice Smith");
        mockedActivity.setUpdatedBy("Alice Smith");
        mockedActivity.setLastupdatedtime(LocalDateTime.now());
        mockedActivity.setComment("added By Alice Smith");
        mockedActivity.setObjectType(ObjectType.TEMPLATE);
        mockedActivity.setObjectID(USER_ID);

        mockedFile = new MockMultipartFile("file", "template.html", "text/html", "This is a test file".getBytes());

        mockedProperties.setProperty("SourceDirectoryOfTemplate", "C:/temp/");
        
        
    }



    @Test
    void testUploadTemplate_UserNotFound() {
        // Arrange
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            templateServiceImpl.uploadTemplate("Test Template", mockedFile, USER_ID);
        });

        assertEquals("User not found with ID: " + USER_ID, exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());

        verify(userRepository, times(1)).findById(USER_ID);
        verify(templateRepository, never()).save(any(Template.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }
    


    @Test
    void testSoftDeleteTemplate_Success() {
        // Arrange
        when(templateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(mockedTemplate));
        when(templateRepository.save(any(Template.class))).thenReturn(mockedTemplate);

        // Act
        TemplateDeleteResponse response = templateServiceImpl.softDeleteTemplate(TEMPLATE_ID);

        // Assert
        assertNotNull(response);
        assertEquals("Template soft deleted successfully", response.getMessage());
        verify(templateRepository, times(1)).findById(TEMPLATE_ID);
        verify(templateRepository, times(1)).save(mockedTemplate);
    }
    

    @Test
    void testDownloadTemplate_TemplateNotFound() {
        // Arrange
        when(templateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        TemplateNotFoundException exception = assertThrows(TemplateNotFoundException.class, () -> {
            templateServiceImpl.downloadTemplate(TEMPLATE_ID);
        });

        assertEquals("Template not found with ID: " + TEMPLATE_ID, exception.getMessage());
        assertEquals("TEMPLATE_NOT_FOUND", exception.getErrorCode());

        verify(templateRepository, times(1)).findById(TEMPLATE_ID);
       // verify(Utility.class, never()).readPropertiesFile();
    }
    

    
    @Test
    void testGetOriginalFilename_Success() {
        // Arrange
        when(templateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(mockedTemplate));

        // Act
        String filename = templateServiceImpl.getOriginalFilename(TEMPLATE_ID);

        // Assert
        assertNotNull(filename);
        assertEquals(mockedTemplate.getTemplateName(), filename);

        verify(templateRepository, times(1)).findById(TEMPLATE_ID);
    }

    @Test
    void testGetOriginalFilename_TemplateNotFound() {
        // Arrange
        when(templateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        TemplateNotFoundException exception = assertThrows(TemplateNotFoundException.class, () -> {
            templateServiceImpl.getOriginalFilename(TEMPLATE_ID);
        });

        assertEquals("Template not found with ID: " + TEMPLATE_ID, exception.getMessage());
        assertEquals("TEMPLATE_NOT_FOUND", exception.getErrorCode());

        verify(templateRepository, times(1)).findById(TEMPLATE_ID);
    }

    @Test
    void testUpdateTemplateDescription_Success() {
        // Arrange
        TemplateUpdateRequest request = new TemplateUpdateRequest();
        request.setNewDescription("Updated Description");

        when(templateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(mockedTemplate));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockedUser));
        when(templateRepository.save(any(Template.class))).thenReturn(mockedTemplate);
        when(activityRepository.save(any(Activity.class))).thenReturn(mockedActivity);

        // Act
        TemplateUpdateResponse response = templateServiceImpl.updateTemplateDescription(TEMPLATE_ID, request, USER_ID);

        // Assert
        assertNotNull(response);
        verify(templateRepository, times(1)).findById(TEMPLATE_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(templateRepository, times(1)).save(mockedTemplate);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void testUpdateTemplateDescription_TemplateNotFound() {
        // Arrange
        TemplateUpdateRequest request = new TemplateUpdateRequest();
        request.setNewDescription("Updated Description");

        when(templateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        TemplateNotFoundException exception = assertThrows(TemplateNotFoundException.class, () -> {
            templateServiceImpl.updateTemplateDescription(TEMPLATE_ID, request, USER_ID);
        });

        assertEquals("Template not found with ID: " + TEMPLATE_ID, exception.getMessage());
        assertEquals("TEMPLATE_NOT_FOUND", exception.getErrorCode());

        verify(templateRepository, times(1)).findById(TEMPLATE_ID);
        verify(userRepository, never()).findById(USER_ID);
        verify(templateRepository, never()).save(any(Template.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void testUpdateTemplateDescription_UserNotFound() {
        // Arrange
        TemplateUpdateRequest request = new TemplateUpdateRequest();
        request.setNewDescription("Updated Description");

        when(templateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(mockedTemplate));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            templateServiceImpl.updateTemplateDescription(TEMPLATE_ID, request, USER_ID);
        });

        assertEquals("User not found with ID: " + USER_ID, exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());

        verify(templateRepository, times(1)).findById(TEMPLATE_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(templateRepository, never()).save(any(Template.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }
    
    @Test
    void testGetAllTemplates_Success() {
        // Arrange
        List<Template> templates = Arrays.asList(mockedTemplate);
        when(templateRepository.findAll()).thenReturn(templates);
        doReturn(true).when(templateServiceImpl).isTemplateFilePresent(any(Template.class));

        // Act
        List<TemplateDTO> result = templateServiceImpl.getAllTemplates();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        TemplateDTO dto = result.get(0);
        assertEquals(mockedTemplate.getTempId(), dto.getTempId());
        assertEquals(mockedTemplate.getTemplateName(), dto.getTemplateName());
        assertEquals(mockedTemplate.getFileSIze(), dto.getFileSize());
        assertEquals(mockedTemplate.getDateTime(), dto.getDateTime());
        assertEquals(mockedTemplate.getTemplateDescription(), dto.getTemplateDescription());

        verify(templateRepository, times(1)).findAll();
    }
    
    @Test
    void testGetAllTemplates_TemplateNotFound() {
        // Arrange
        when(templateRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<TemplateDTO> result = templateServiceImpl.getAllTemplates();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(templateRepository, times(1)).findAll();
    } 
    
}





