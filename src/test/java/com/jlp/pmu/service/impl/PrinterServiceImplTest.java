package com.jlp.pmu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.jlp.pmu.dto.PrinterNamesListRequset;
import com.jlp.pmu.dto.PrinterRequest;
import com.jlp.pmu.dto.PrinterResponse;
import com.jlp.pmu.dto.RedirectPrinterRequest;
import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.exception.PrinterAlreadyExistException;
import com.jlp.pmu.exception.PrinterNotFoundException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Printer;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.PrinterRepository;
import com.jlp.pmu.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class PrinterServiceImplTest {

    @Mock
    private PrinterRepository printerRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PrinterServiceImpl printerService;

    private PrinterRequest mockedPrinterRequest;
    private PrinterResponse mockedPrinterResponse;
    private Printer mockedPrinter;
    private User mockedUser;
    private static final Logger log = LogManager.getLogger(PrinterServiceImplTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedPrinterRequest = PrinterRequest.builder()
                .userId("K8976")
                .printerID(1L)
                .printerName("Test Printer")
                .branchCode(123L)
                .pmuPrinterName("PMU Test Printer")
                .printerPATH("test/path")
                .printerType(PrinterType.LASER)
                .comments("Test comments")
                .pmuServer("Test Server")
                .localAttachedPrinter(true)
                .build();

        mockedPrinter = Printer.builder()
                .printerID(1L)
                .branchCode(123L)
                .createdBy("John Doe")
                .updatedBy("John Doe")
                .status(true)
                .lastUpdatedTime(LocalDateTime.now())
                .printerName("Test Printer")
                .pmuPrinterName("PMU Test Printer")
                .printerPATH("test/path")
                .printerType(PrinterType.LASER)
                .comments("Test comments")
                .pmuServer("Test Server")
                .localAttachedPrinter(true)
                .build();

        //PrinterResponse mockedPrinterResponse = new PrinterResponse();
        mockedPrinterResponse = new PrinterResponse();
        mockedPrinterResponse.setPrinterID(1L);
        mockedPrinterResponse.setPrinterName("Test Printer");
        mockedPrinterResponse.setPmuPrinterName("PMU Test Printer");
        mockedPrinterResponse.setPrinterPATH("test/path");
        mockedPrinterResponse.setPrinterType(PrinterType.LASER);
        mockedPrinterResponse.setComments("Test comments");
        mockedPrinterResponse.setPmuServer("Test Server");
        mockedPrinterResponse.setLocalAttachedPrinter(true);

        mockedUser = new User();
        mockedUser.setUserId("K8976");
        mockedUser.setFirstName("John");
        mockedUser.setLastName("Doe");
    }

    @Test
    void testAddPrinterDetails_UserNotFound() {
        // Mocking repository behavior
        when(userRepository.findById("K8976")).thenReturn(Optional.empty());

        // Invoke the service method and assert exception
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            printerService.addPrinterDetails(mockedPrinterRequest);
        });

        assertEquals("The User was not found with given ID: K8976", exception.getMessage());

        // Verifying interactions
        verify(userRepository, times(1)).findById("K8976");
        verify(printerRepository, never()).existsByPrinterNameAndBranchCode(anyString(), anyLong());
        verify(printerRepository, never()).save(any(Printer.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }
    
    @Test
    void testAddPrinterDetails_Success() {
        // Mocking repository behavior
        when(userRepository.findById("K8976")).thenReturn(Optional.of(mockedUser));
        when(printerRepository.existsByPrinterNameAndBranchCode("Test Printer", 123L)).thenReturn(false);
        when(printerRepository.save(any(Printer.class))).thenReturn(mockedPrinter);
        when(modelMapper.map(mockedPrinter, PrinterResponse.class)).thenReturn(mockedPrinterResponse);

        // Invoke the service method
        PrinterResponse response = printerService.addPrinterDetails(mockedPrinterRequest);

        // Assert the response
        assertNotNull(response);
        assertEquals(mockedPrinterResponse.getPrinterID(), response.getPrinterID());
        assertEquals(mockedPrinterResponse.getPrinterName(), response.getPrinterName());
        assertEquals(mockedPrinterResponse.getPmuPrinterName(), response.getPmuPrinterName());

        // Verifying interactions
        verify(userRepository, times(1)).findById("K8976");
        verify(printerRepository, times(1)).existsByPrinterNameAndBranchCode("Test Printer", 123L);
        verify(printerRepository, times(1)).save(any(Printer.class));
        verify(activityRepository, times(1)).save(any(Activity.class));
    }


    @Test
    void testAddPrinterDetails_PrinterAlreadyExists() {
        // Mocking repository behavior
        when(userRepository.findById("K8976")).thenReturn(Optional.of(mockedUser));
        when(printerRepository.existsByPrinterNameAndBranchCode("Test Printer", 123L)).thenReturn(true);

        // Invoke the service method and assert exception
        PrinterAlreadyExistException exception = assertThrows(PrinterAlreadyExistException.class, () -> {
            printerService.addPrinterDetails(mockedPrinterRequest);
        });

        assertEquals("printer is already exists given printername Test Printer and branchcode 123", exception.getMessage());

        // Verifying interactions
        verify(userRepository, times(1)).findById("K8976");
        verify(printerRepository, times(1)).existsByPrinterNameAndBranchCode("Test Printer", 123L);
        verify(printerRepository, never()).save(any(Printer.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void testUpdatePrinterDetails_PrinterNotFound() {
        // Mocking repository behavior
        when(userRepository.findById("K8976")).thenReturn(Optional.of(mockedUser));
        when(printerRepository.findById(1L)).thenReturn(Optional.empty());

        // Invoke the service method and assert exception
        PrinterNotFoundException exception = assertThrows(PrinterNotFoundException.class, () -> {
            printerService.updatePrinterDetails(mockedPrinterRequest);
        });

        assertEquals("The Printer was not found with given ID: 1", exception.getMessage());

        // Verifying interactions
        verify(userRepository, times(1)).findById("K8976");
        verify(printerRepository, times(1)).findById(1L);
        verify(printerRepository, never()).save(any(Printer.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }
    
    @Test
    void testUpdatePrinterDetails_Success() {
        // Arrange
        when(userRepository.findById("K8976")).thenReturn(Optional.of(mockedUser));
        when(printerRepository.findById(1L)).thenReturn(Optional.of(mockedPrinter));
        when(printerRepository.save(any(Printer.class))).thenReturn(mockedPrinter);
        when(modelMapper.map(mockedPrinter, PrinterResponse.class)).thenReturn(mockedPrinterResponse);

        // Act
        PrinterResponse response = printerService.updatePrinterDetails(mockedPrinterRequest);

        // Assert
        assertNotNull(response); // Ensure response is not null
        assertEquals(mockedPrinterResponse.getPrinterID(), response.getPrinterID());
        assertEquals(mockedPrinterResponse.getPrinterName(), response.getPrinterName());
        assertEquals(mockedPrinterResponse.getPmuPrinterName(), response.getPmuPrinterName());

        // Verifying interactions
        verify(userRepository, times(1)).findById("K8976");
        verify(printerRepository, times(1)).findById(1L);
        verify(printerRepository, times(1)).save(any(Printer.class));
        verify(activityRepository, times(1)).save(any(Activity.class));
    }
    
    @Test
    void testDoInactivePrinterByprinterID_UserNotFound() {
        // Mocking repository behavior
        when(userRepository.findById("K8976")).thenReturn(Optional.empty());

        // Invoke the service method and assert exception
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            printerService.doInactivePrinterByprinterID(mockedPrinterRequest);
        });

        assertEquals("The User was not found with given ID: K8976", exception.getMessage());

        // Verifying interactions
        verify(userRepository, times(1)).findById("K8976");
        verify(printerRepository, never()).findById(anyLong());
        verify(printerRepository, never()).save(any(Printer.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }
    

    @Test
    void testDoInactivePrinterByprinterID_PrinterNotFound() {
        // Mocking repository behavior
        when(userRepository.findById("K8976")).thenReturn(Optional.of(mockedUser));
        when(printerRepository.findById(1L)).thenReturn(Optional.empty());

        // Invoke the service method and assert exception
        PrinterNotFoundException exception = assertThrows(PrinterNotFoundException.class, () -> {
            printerService.doInactivePrinterByprinterID(mockedPrinterRequest);
        });

        assertEquals("The Printer with given code: 1 was not found", exception.getMessage());

        // Verifying interactions
        verify(userRepository, times(1)).findById("K8976");
        verify(printerRepository, times(1)).findById(1L);
        verify(printerRepository, never()).save(any(Printer.class));
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    public void testGetPrinterNamesList_success() {
        String userId = "K8976";
        Long branchCode = 123L;
        String printerName = "Test Printer";

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(printerRepository.findPrinterTypeByPrinterNameAndBranchCode(printerName, branchCode)).thenReturn(PrinterType.LASER);
        when(printerRepository.findByCombinedPrintTypeAndBranchCode(true,PrinterType.LASER, branchCode)).thenReturn(Arrays.asList("Printer1", "Printer2"));

        List<String> printerNames = printerService.getPrinterNamesList(userId, branchCode, printerName);

        assertNotNull(printerNames);
        assertEquals(2, printerNames.size());
        assertEquals("Printer1", printerNames.get(0));
        assertEquals("Printer2", printerNames.get(1));

        verify(userRepository, times(1)).findById(userId);
        verify(printerRepository, times(1)).findPrinterTypeByPrinterNameAndBranchCode(printerName, branchCode);
        verify(printerRepository, times(1)).findByCombinedPrintTypeAndBranchCode(true,PrinterType.LASER, branchCode);
    }
    
//    @Test
//    public void testGetPrinterNamesList_userNotFound() {
//        String userId = "K8976";
//        Long branchCode = 123L;
//        String printerName = "Test Printer";
//
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> {
//            printerService.getPrinterNamesList(userId, branchCode, printerName);
//        });
//
//        verify(userRepository, times(1)).findById(userId);
//        verify(printerRepository, times(0)).findPrinterTypeByPrinterNameAndBranchCode(anyString(), anyLong());
//        verify(printerRepository, times(0)).findByCombinedPrintTypeAndBranchCode(true,any(PrinterType.class), anyLong());
//    }

    @Test
    public void testGetAllPrinterTypes_success() {
        Long branchCode = 123L;
        List<PrinterType> printerTypes = Arrays.asList(PrinterType.LASER, PrinterType.ZEBRA);

        when(printerRepository.findAllPrinterTypes(branchCode)).thenReturn(printerTypes);

        List<PrinterType> result = printerService.getAllPrinterTypes(branchCode);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(PrinterType.LASER, result.get(0));
        assertEquals(PrinterType.ZEBRA, result.get(1));

        verify(printerRepository, times(1)).findAllPrinterTypes(branchCode);
    }


    @Test
    public void testUpdateRedirectPrinter_Success() {
        // Mocking userRepository.findById()
        User user = new User();
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        // Mocking printerRepository.findByBranchCodeAndPrinterName()
        Printer printer = new Printer();
        when(printerRepository.findByBranchCodeAndPrinterName(anyLong(), anyString())).thenReturn(Optional.of(printer));

        // Mocking printerRepository.save()
        Printer updatedPrinter = new Printer();
        updatedPrinter.setRedirectPrintName("RedirectPrintName");
        updatedPrinter.setRedirectExist(true);

        when(printerRepository.save(any(Printer.class))).thenReturn(updatedPrinter);

        // Mocking modelMapper.map()
        PrinterResponse expectedResponse = new PrinterResponse();
        when(modelMapper.map(any(Printer.class), eq(PrinterResponse.class))).thenReturn(expectedResponse);

        // Creating the request object using Lombok builder
        RedirectPrinterRequest request = RedirectPrinterRequest.builder()
                .userId("user123")
                .branchCode(123L)
                .printerName("PrinterName")
                .redirectPrintName("RedirectPrintName")
                .build();

        // Calling the method
        PrinterResponse result = printerService.updateRedirectPrinter(request);

        // Assertions
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        // Verify interactions
        verify(userRepository, times(1)).findById(anyString());
        verify(printerRepository, times(1)).findByBranchCodeAndPrinterName(anyLong(), anyString());
        verify(printerRepository, times(1)).save(any(Printer.class));
        verify(modelMapper, times(1)).map(any(Printer.class), eq(PrinterResponse.class));
    }
  

  @Test
  public void testUpdateRedirectPrinter_UserNotFoundException() {
      // Mocking userRepository.findById() to return empty Optional
      when(userRepository.findById(any())).thenReturn(Optional.empty());

      // Creating the request object using Lombok builder
      RedirectPrinterRequest request = RedirectPrinterRequest.builder()
              .userId("user123")
              .branchCode(123L)
              .printerName("PrinterName")
              .redirectPrintName("RedirectPrintName")
              .build();

      // Calling the method and expecting UserNotFoundException
      assertThrows(UserNotFoundException.class, () -> printerService.updateRedirectPrinter(request));
  }

  @Test
  public void testUpdateRedirectPrinter_PrinterNotFoundException() {
      // Mocking userRepository.findById()
      User user = new User();
      when(userRepository.findById(any())).thenReturn(Optional.of(user));

      // Mocking printerRepository.findByBranchCodeAndPrinterName() to return empty Optional
      when(printerRepository.findByBranchCodeAndPrinterName(any(), any())).thenReturn(Optional.empty());

      // Creating the request object using Lombok builder
      RedirectPrinterRequest request = RedirectPrinterRequest.builder()
              .userId("user123")
              .branchCode(123L)
              .printerName("PrinterName")
              .redirectPrintName("RedirectPrintName")
              .build();

      // Calling the method and expecting PrinterNotFoundException
      assertThrows(PrinterNotFoundException.class, () -> printerService.updateRedirectPrinter(request));
  }
  @Test
  public void testRemoveRedirectionPrinter_Success() {
      // Mocking userRepository.findById()
      User user = new User();
      when(userRepository.findById(any())).thenReturn(Optional.of(user));

      // Mocking printerRepository.findByBranchCodeAndPrinterName()
      Printer printer = new Printer();
      printer.setRedirectPrintName("RedirectPrintName");
      printer.setRedirectExist(true);

      when(printerRepository.findByBranchCodeAndPrinterName(anyLong(), anyString())).thenReturn(Optional.of(printer));

      // Mocking printerRepository.save()
      Printer savedPrinter = new Printer();
      savedPrinter.setRedirectPrintName(null);
      savedPrinter.setRedirectExist(false);

      when(printerRepository.save(any(Printer.class))).thenReturn(savedPrinter);

      // Mocking modelMapper.map()
      PrinterResponse expectedResponse = new PrinterResponse();
      when(modelMapper.map(any(Printer.class), eq(PrinterResponse.class))).thenReturn(expectedResponse);

      // Creating the request object using Lombok builder
      RedirectPrinterRequest request = RedirectPrinterRequest.builder()
              .userId("user123")
              .branchCode(123L)
              .printerName("PrinterName")
              .redirectPrintName("RedirectPrintName")
              .build();

      // Calling the method
      PrinterResponse result = printerService.removeRedirectionPrinter(request);

      // Assertions
      assertNotNull(result);
      assertEquals(expectedResponse, result);

      // Verify interactions
      verify(userRepository, times(1)).findById(anyString());
      verify(printerRepository, times(1)).findByBranchCodeAndPrinterName(anyLong(), anyString());
      verify(printerRepository, times(1)).save(any(Printer.class));
      verify(modelMapper, times(1)).map(any(Printer.class), eq(PrinterResponse.class));
  }
  


  @Test
  public void testRemoveRedirectionPrinter_UserNotFoundException() {
      // Mocking userRepository.findById() to return empty Optional
      when(userRepository.findById(any())).thenReturn(Optional.empty());

      // Creating the request object using Lombok builder
      RedirectPrinterRequest request = RedirectPrinterRequest.builder()
              .userId("user123")
              .branchCode(123L)
              .printerName("PrinterName")
              .redirectPrintName("RedirectPrintName")
              .build();

      // Calling the method and expecting UserNotFoundException
      assertThrows(UserNotFoundException.class, () -> printerService.removeRedirectionPrinter(request));
  }

  @Test
  public void testRemoveRedirectionPrinter_PrinterNotFoundException() {
      // Mocking userRepository.findById()
      User user = new User();
      when(userRepository.findById(any())).thenReturn(Optional.of(user));

      // Mocking printerRepository.findByBranchCodeAndPrinterName() to return empty Optional
      when(printerRepository.findByBranchCodeAndPrinterName(any(), any())).thenReturn(Optional.empty());

      // Creating the request object using Lombok builder
      RedirectPrinterRequest request = RedirectPrinterRequest.builder()
              .userId("user123")
              .branchCode(123L)
              .printerName("PrinterName")
              .redirectPrintName("RedirectPrintName")
              .build();

      // Calling the method and expecting PrinterNotFoundException
      assertThrows(PrinterNotFoundException.class, () -> printerService.removeRedirectionPrinter(request));
  }
  

  @Test
  void testGetListOfPrinterManagement1() {
      // Mock data
      List<Printer> printers = new ArrayList<>();
      

      // Mock repository method
      when(printerRepository.findByStatusAndBranchCodeOrderbydate(true, 123L)).thenReturn(printers);

      // Mock modelMapper
      List<PrinterResponse> expectedResponses = printers.stream()
              .map(printer -> modelMapper.map(printer, PrinterResponse.class))
              .collect(Collectors.toList());
      //when(modelMapper.map(any(Printer.class), eq(PrinterResponse.class))).thenReturn(PrinterResponse.builder().build());

      // Invoke service method
      List<PrinterResponse> actualResponses = printerService.getListOfPrinterManagement(123L);

      // Assertions
      assertNotNull(actualResponses);
      assertEquals(expectedResponses.size(), actualResponses.size());
      for (int i = 0; i < expectedResponses.size(); i++) {
          assertEquals(expectedResponses.get(i), actualResponses.get(i));
      }

      // Verify interactions
      verify(printerRepository, times(1)).findByStatusAndBranchCodeOrderbydate(true, 123L);
      verify(modelMapper, times(printers.size())).map(any(Printer.class), eq(PrinterResponse.class));
  }
}

