package com.jlp.pmu.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.jlp.pmu.dto.BranchRequest;
import com.jlp.pmu.dto.BranchResponse;
import com.jlp.pmu.exception.BranchAlreadyExistException;
import com.jlp.pmu.exception.BranchNotFoundException;
import com.jlp.pmu.exception.MnemonicAlreadyExistException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Activity;
import com.jlp.pmu.models.Branch;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.BranchRepository;
import com.jlp.pmu.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

	@Mock
	BranchRepository branchRepository;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	ActivityRepository activityRepository;
	
	@Mock
	ModelMapper modelMapper;
	
	@InjectMocks
	BranchServiceImpl branchServiceImpl = new BranchServiceImpl();
	
	BranchRequest mockedBranchRequestObject = null;
	BranchResponse mockedBranchResponseObject = new BranchResponse();
	Branch mockedBranchObject = new Branch();
	User mockedUserObject = new User();
	
	@BeforeEach
	public void setup() {
	
	// setup branch request object
		mockedBranchRequestObject = BranchRequest.builder().
				userId("J12345").
				branchCode(42L).
				branchName("London").
				mnemonic("LD").
				pmuServers(new ArrayList<String>(Arrays.asList("GW-S-AB1","GW-S-AF1","GW-S-AK1"))).
				build();
	
	// setup branch object	
		mockedBranchObject.setBranchCode(42L);
		mockedBranchObject.setBranchName("London");
		mockedBranchObject.setMnemonic("LD");
		mockedBranchObject.setPmuServers(new ArrayList<String>(Arrays.asList("GW-S-AB1","GW-S-AF1")));
		mockedBranchObject.setBranchStatus(true);
		mockedBranchObject.setComment("added by David");
		mockedBranchObject.setCreatedBy("David Warner");
		mockedBranchObject.setLastUpdatedTime(LocalDateTime.now());
		mockedBranchObject.setUpdatedBy("David Warner");
		
	// setup user object	
		mockedUserObject.setFirstName("David");
		mockedUserObject.setLastName("Warner");
		mockedUserObject.setUserId("J12345");
		mockedUserObject.setEmailAddress("david@gmail.com");
		
	// setup mockedBranchResponseObject
		mockedBranchResponseObject.setBranchCode(42L);
		mockedBranchResponseObject.setBranchName("London");
		mockedBranchResponseObject.setMnemonic("LD");
		mockedBranchResponseObject.setPmuServers(Arrays.asList("GW-S-AB1","GW-S-AF1"));
		mockedBranchResponseObject.setComment("added by David");
	}
	
	@Test
	public void addBranchDetails_success_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(mockedUserObject));
		when(branchRepository.findByBranchCodeOrMnemonic(anyLong(), anyString())).thenReturn(Collections.emptyList());
		when(branchRepository.save(any(Branch.class))).thenReturn(mockedBranchObject);
		when(modelMapper.map(mockedBranchObject, BranchResponse.class)).thenReturn(mockedBranchResponseObject);
		
		BranchResponse response = branchServiceImpl.addBranchDetails(mockedBranchRequestObject);
		
		assertNotNull(response);
		
		verify(userRepository, times(1)).findById(anyString());
		verify(branchRepository, times(1)).findByBranchCodeOrMnemonic(anyLong(), anyString());
		verify(branchRepository, times(1)).save(any(Branch.class));
		verify(activityRepository, times(1)).save(any(Activity.class));
		verify(modelMapper, times(1)).map(mockedBranchObject, BranchResponse.class);
	}
	
	@Test
	public void addBranchDetails_UserNotFoundException_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.empty());
		
		UserNotFoundException exception = assertThrows(UserNotFoundException.class,() -> branchServiceImpl.addBranchDetails(mockedBranchRequestObject));
		
		assertEquals("USER_NOT_FOUND", exception.getErrorCode());
		assertEquals("The User was not found with given ID: "+mockedBranchRequestObject.getUserId(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(branchRepository, never()).findByBranchCodeOrMnemonic(anyLong(), anyString());
	}
	
	@Test 
	public void addBranchDetails_BranchAlreadyExistException_test() {
		
		BranchRequest branchDto = mockedBranchRequestObject;
		Branch branch = mockedBranchObject;
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(mockedUserObject));
		when(branchRepository.findByBranchCodeOrMnemonic(anyLong(), anyString())).thenReturn(List.of(branch));
		
		BranchAlreadyExistException exception = assertThrows(BranchAlreadyExistException.class, () -> branchServiceImpl.addBranchDetails(branchDto));
	
		assertEquals("BRANCH_ALREADY_EXISTS", exception.getErrorCode());
		assertEquals("Branch Already Exist with given branchCode: "+branchDto.getBranchCode(), exception.getMessage());
	
		verify(userRepository, times(1)).findById(anyString());
		verify(branchRepository, times(1)).findByBranchCodeOrMnemonic(anyLong(), anyString());
		verify(branchRepository, never()).save(any(Branch.class));
	}
	
	
	@Test 
	public void addBranchDetails_MnemonicAlreadyExistException_test() {
		
		BranchRequest branchDto = mockedBranchRequestObject;
		
		Branch branch = mockedBranchObject;
		branch.setBranchCode(43L);
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(mockedUserObject));
		when(branchRepository.findByBranchCodeOrMnemonic(anyLong(), anyString())).thenReturn(List.of(branch));
		
		MnemonicAlreadyExistException exception = assertThrows(MnemonicAlreadyExistException.class, () -> branchServiceImpl.addBranchDetails(branchDto));
	
		assertEquals("MNEMONIC_ALREADY_EXISTS", exception.getErrorCode());
		assertEquals("Branch Already Exist with given Mnemonic: "+branchDto.getMnemonic(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(branchRepository, times(1)).findByBranchCodeOrMnemonic(anyLong(), anyString());
		verify(branchRepository, never()).save(any(Branch.class));
	
	}
	
	
	@Test
	public void getBranchDetailsByID_success_test() {
		when(branchRepository.findById(anyLong())).thenReturn(Optional.of(mockedBranchObject));
		when(modelMapper.map(mockedBranchObject, BranchResponse.class)).thenReturn(mockedBranchResponseObject);
		
		BranchResponse response = branchServiceImpl.getBranchDetailsByID(42L);
		
		assertNotNull(response);
		
		verify(branchRepository, times(1)).findById(anyLong());
		verify(modelMapper, times(1)).map(mockedBranchObject, BranchResponse.class);
	}
	
	@Test
	public void getBranchDetailsByID_BranchNotFoundException_test() {
		
		Long branchCode = 42L;
		when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		BranchNotFoundException exception = assertThrows(BranchNotFoundException.class,() -> branchServiceImpl.getBranchDetailsByID(branchCode));
		
		assertEquals("BRANCH_NOT_FOUND", exception.getErrorCode());
		assertEquals("The Branch with given code: " + branchCode + " was not found", exception.getMessage());
		
		verify(branchRepository, times(1)).findById(anyLong());
		verify(modelMapper, never()).map(mockedBranchObject, BranchResponse.class);
	}
	
	@Test
	public void getAllBranchDeatils_success_test() {
		when(branchRepository.findByBranchStatus(any(Boolean.class))).thenReturn(List.of(mockedBranchObject));
		when(modelMapper.map(mockedBranchObject, BranchResponse.class)).thenReturn(mockedBranchResponseObject);
		
		List<BranchResponse> response = branchServiceImpl.getAllBranchDeatils();
		
		assertNotNull(response);
		assertFalse(response.isEmpty());
		
		verify(branchRepository, times(1)).findByBranchStatus(any(Boolean.class));
		verify(modelMapper, times(1)).map(mockedBranchObject, BranchResponse.class);
	}
	
	@Test
	public void getAllBranchDeatils_Exception_test() {
		when(branchRepository.findByBranchStatus(any(Boolean.class))).thenThrow(RuntimeException.class);
		branchServiceImpl.getAllBranchDeatils();
	}
	
	@Test
	public void doInactiveBranchByBranchCode_success_test() {
		
		when(userRepository.findById(anyString())).thenReturn(Optional.of(mockedUserObject));
		when(branchRepository.findById(anyLong())).thenReturn(Optional.of(mockedBranchObject));
		when(branchRepository.save(any(Branch.class))).thenReturn(mockedBranchObject);
		when(modelMapper.map(mockedBranchObject, BranchResponse.class)).thenReturn(mockedBranchResponseObject);
		
		BranchResponse response = branchServiceImpl.doInactiveBranchByBranchCode(mockedBranchRequestObject);
		
		assertNotNull(response);
		
		verify(userRepository, times(1)).findById(anyString());
		verify(branchRepository, times(1)).findById(anyLong());
		verify(branchRepository, times(1)).save(any(Branch.class));
		verify(activityRepository, times(1)).save(any(Activity.class));
		verify(modelMapper, times(1)).map(mockedBranchObject, BranchResponse.class);
		
	
	}
	
	@Test
	public void doInactiveBranchByBranchCode_BranchNotFoundException_test() {
		
		when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());
		when(userRepository.findById(anyString())).thenReturn(Optional.of(mockedUserObject));
		
		BranchNotFoundException exception = assertThrows(BranchNotFoundException.class,() -> branchServiceImpl.doInactiveBranchByBranchCode(mockedBranchRequestObject));
		
		assertEquals("BRANCH_NOT_FOUND", exception.getErrorCode());
		assertEquals("The Branch with given code: " + mockedBranchRequestObject.getBranchCode() + " was not found", exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(branchRepository, times(1)).findById(anyLong());
		verify(branchRepository, never()).save(any(Branch.class));
	}
	
	@Test
	public void doInactiveBranchByBranchCode_UserNotFoundException_test() {
		BranchRequest branch = mockedBranchRequestObject;
		
		when(userRepository.findById(anyString())).thenReturn(Optional.empty());
		
		UserNotFoundException exception = assertThrows(UserNotFoundException.class,() -> branchServiceImpl.doInactiveBranchByBranchCode(branch));
		
		assertEquals("USER_NOT_FOUND", exception.getErrorCode());
		assertEquals("The User was not found with given ID: "+branch.getUserId(), exception.getMessage());
		
		verify(userRepository, times(1)).findById(anyString());
		verify(branchRepository, never()).findById(anyLong());
	}
	
	@Test
	public void getPMUServersByBranchID_success_test() {
		Long branchCode = 42L;
		Branch branch = mockedBranchObject;
		
		when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));
		
		List<String> response = branchServiceImpl.getPMUServersByBranchID(branchCode);
		
		assertNotNull(response);
		assertFalse(response.isEmpty());
		assertThat(response.size()).isGreaterThan(0);
		
		verify(branchRepository, times(1)).findById(anyLong());
	}
	
	@Test
	public void getPMUServersByBranchID_BranchNotFoundException_test() {
		
		Long branchCode = 42L;
		when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		BranchNotFoundException exception = assertThrows(BranchNotFoundException.class,() -> branchServiceImpl.getPMUServersByBranchID(branchCode));
		
		assertEquals("BRANCH_NOT_FOUND", exception.getErrorCode());
		assertEquals("The Branch with given code: " + branchCode + " was not found", exception.getMessage());
		
		verify(branchRepository, times(1)).findById(anyLong());
	}
	
	
	@Test
	public void getAllPMUServers_success_test() {
		when(branchRepository.findAllPMUServers()).thenReturn(Arrays.asList("AS-S-EW1", "GH-S-AW1"));
		
		List<String> response = branchServiceImpl.getAllPMUServers();
		
		assertNotNull(response);
		assertFalse(response.isEmpty());
		assertThat(response.size()).isGreaterThan(0);
		
		verify(branchRepository, times(1)).findAllPMUServers();
	}
	
	@Test
	public void getAllMnemonic_success_test() {
		when(branchRepository.findAllMnemonic()).thenReturn(Arrays.asList("AS", "GH"));
		
		List<String> response = branchServiceImpl.getAllMnemonic();
		
		assertNotNull(response);
		assertFalse(response.isEmpty());
		assertThat(response.size()).isGreaterThan(0);
		
		verify(branchRepository, times(1)).findAllMnemonic();
	}
	
}
