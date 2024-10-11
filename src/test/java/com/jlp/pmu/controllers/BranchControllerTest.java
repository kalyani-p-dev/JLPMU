package com.jlp.pmu.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlp.pmu.dto.BranchRequest;
import com.jlp.pmu.dto.BranchResponse;
import com.jlp.pmu.exception.BranchAlreadyExistException;
import com.jlp.pmu.exception.BranchNotFoundException;
import com.jlp.pmu.exception.MnemonicAlreadyExistException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.exception.handler.BranchServiceExceptionHandler;
import com.jlp.pmu.exception.handler.UserServiceExceptionController;
import com.jlp.pmu.service.BranchService;

@WebMvcTest
@ContextConfiguration(classes = {BranchController.class, BranchServiceExceptionHandler.class, UserServiceExceptionController.class})
class BranchControllerTest {

	@MockBean
	private BranchService branchService;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	private BranchRequest mockedBranchRequestObject = null;
	private BranchResponse mockedBranchResponseObject = new BranchResponse();
	
	@BeforeEach
	void setup() {
		
		//Init MockMvc Object and build
		 mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		// setup branch request object
				mockedBranchRequestObject = BranchRequest.builder().
						userId("J12345").
						branchCode(42L).
						branchName("London").
						mnemonic("LD").
						pmuServers(new ArrayList<String>(Arrays.asList("GW-S-AB1","GW-S-AF1","GW-S-AK1"))).
						build();
				
		// setup mockedBranchResponseObject
				mockedBranchResponseObject.setBranchCode(42L);
				mockedBranchResponseObject.setBranchName("London");
				mockedBranchResponseObject.setMnemonic("LD");
				mockedBranchResponseObject.setPmuServers(Arrays.asList("GW-S-AB1","GW-S-AF1"));
				mockedBranchResponseObject.setComment("added by David");
	}
	
	@Test
	public void addBranchDetails_success_test() throws Exception{
		
		when(branchService.addBranchDetails(any(BranchRequest.class))).thenReturn(mockedBranchResponseObject);
		
		ResultActions response =  mockMvc.perform(
							post("/pmu/v1/api/branch/add-branch")
							.characterEncoding(Charset.defaultCharset())
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(mockedBranchRequestObject))
						);
		
		response.andExpect(status().isCreated())
				.andDo(print())
				.andExpect(jsonPath("$.branchName", is(mockedBranchResponseObject.getBranchName())))
				.andExpect(jsonPath("$.mnemonic", is(mockedBranchResponseObject.getMnemonic())));
	}
	
	@Test
	public void addBranchDetails_UserNotFoundException_test() throws Exception{
		
		when(branchService.addBranchDetails(any(BranchRequest.class))).thenThrow(new UserNotFoundException(
				"The User was not found with given ID: " + mockedBranchRequestObject.getUserId(), "USER_NOT_FOUND"));
		
		ResultActions response =  mockMvc.perform(
				post("/pmu/v1/api/branch/add-branch")
				.characterEncoding(Charset.defaultCharset())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockedBranchRequestObject))
			);
		
		response.andExpect(status().isNotFound())
		.andDo(print())
		.andExpect(jsonPath("$.message", is("The User was not found with given ID: " + mockedBranchRequestObject.getUserId())))
		.andExpect(jsonPath("$.errorCode", is("USER_NOT_FOUND")));
	}
	
	@Test
	public void addBranchDetails_BranchAlreadyExistException_test() throws Exception{
		
		when(branchService.addBranchDetails(any(BranchRequest.class))).thenThrow(new BranchAlreadyExistException("Branch Already Exist with given branchCode: " + mockedBranchRequestObject.getBranchCode(),"BRANCH_ALREADY_EXISTS"));
		
		ResultActions response =  mockMvc.perform(
				post("/pmu/v1/api/branch/add-branch")
				.characterEncoding(Charset.defaultCharset())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockedBranchRequestObject))
			);
		
		response.andExpect(status().isAlreadyReported())
		.andDo(print())
		.andExpect(jsonPath("$.message", is("Branch Already Exist with given branchCode: " + mockedBranchRequestObject.getBranchCode())))
		.andExpect(jsonPath("$.errorCode", is("BRANCH_ALREADY_EXISTS")));
	}
	
	@Test
	public void addBranchDetails_MnemonicAlreadyExistException_test() throws Exception{
		
		when(branchService.addBranchDetails(any(BranchRequest.class))).thenThrow(new MnemonicAlreadyExistException("Branch Already Exist with given Mnemonic: " + mockedBranchRequestObject.getMnemonic(),"MNEMONIC_ALREADY_EXISTS"));
		
		ResultActions response =  mockMvc.perform(
				post("/pmu/v1/api/branch/add-branch")
				.characterEncoding(Charset.defaultCharset())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockedBranchRequestObject))
			);
		
		response.andExpect(status().isAlreadyReported())
		.andDo(print())
		.andExpect(jsonPath("$.message", is("Branch Already Exist with given Mnemonic: " + mockedBranchRequestObject.getMnemonic())))
		.andExpect(jsonPath("$.errorCode", is("MNEMONIC_ALREADY_EXISTS")));
	}
	
	
	@Test
	public void getAllBranchDetails_success_test() throws Exception{
		
		BranchResponse branch = mockedBranchResponseObject;
		
		BranchResponse branch2 = new BranchResponse();
		branch2.setBranchCode(43L);
		branch2.setBranchName("London");
		branch2.setMnemonic("LO");
		branch2.setPmuServers(Arrays.asList("GW-S-AB1","GW-S-AF1"));
		branch2.setComment("added by David");
		
		
		when(branchService.getAllBranchDeatils()).thenReturn(List.of(branch, branch2));
		
		ResultActions response =  mockMvc.perform(get("/pmu/v1/api/branch/get-all-branch"));
		
		response.andExpect(status().isOk())
		.andDo(print())
		.andExpect(jsonPath("$.size()", is(2)));
	}
	
	@Test
	public void getBranchDetailsByID_success_test() throws Exception{
		
		when(branchService.getBranchDetailsByID(anyLong())).thenReturn(mockedBranchResponseObject);
		
		ResultActions response =  mockMvc.perform(get("/pmu/v1/api/branch/get-branch/{branchCode}", mockedBranchRequestObject.getBranchCode()));
		
		response.andExpect(status().isOk())
		.andDo(print())
		.andExpect(jsonPath("$.branchName", is(mockedBranchResponseObject.getBranchName())))
		.andExpect(jsonPath("$.mnemonic", is(mockedBranchResponseObject.getMnemonic())));

	}
	
	@Test
	public void getBranchDetailsByID_BranchNotFoundException_test() throws Exception{
		
		when(branchService.getBranchDetailsByID(anyLong())).thenThrow(new BranchNotFoundException ("The Branch with given code: " + mockedBranchRequestObject.getBranchCode() + " was not found" , "BRANCH_NOT_FOUND"));
		
		ResultActions response =  mockMvc.perform(
										get("/pmu/v1/api/branch/get-branch/{branchCode}", mockedBranchRequestObject.getBranchCode())
								  );
		
		
		response.andExpect(status().isNotFound())
		.andDo(print())
		.andExpect(jsonPath("$.message", is("The Branch with given code: " + mockedBranchRequestObject.getBranchCode() + " was not found")))
		.andExpect(jsonPath("$.errorCode", is("BRANCH_NOT_FOUND")));

	}
	
	@Test
	public void doInactiveBranchByBranchCode_success_test() throws Exception {

		when(branchService.doInactiveBranchByBranchCode(any(BranchRequest.class)))
				.thenReturn(mockedBranchResponseObject);

		ResultActions response = mockMvc.perform(put("/pmu/v1/api/branch//inactive")
				.characterEncoding(Charset.defaultCharset()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockedBranchRequestObject)));

		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.branchName", is(mockedBranchResponseObject.getBranchName())))
				.andExpect(jsonPath("$.mnemonic", is(mockedBranchResponseObject.getMnemonic())));
	}
	
	@Test
	public void doInactiveBranchByBranchCode_BranchNotFoundException_test() throws Exception{
		
		when(branchService.doInactiveBranchByBranchCode(any(BranchRequest.class))).thenThrow(new BranchNotFoundException ("The Branch with given code: " + mockedBranchRequestObject.getBranchCode() + " was not found" , "BRANCH_NOT_FOUND"));
		
		ResultActions response = mockMvc.perform(put("/pmu/v1/api/branch//inactive")
				.characterEncoding(Charset.defaultCharset()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockedBranchRequestObject)));
		
		
		response.andExpect(status().isNotFound())
		.andDo(print())
		.andExpect(jsonPath("$.message", is("The Branch with given code: " + mockedBranchRequestObject.getBranchCode() + " was not found")))
		.andExpect(jsonPath("$.errorCode", is("BRANCH_NOT_FOUND")));

	}

	@Test
	public void doInactiveBranchByBranchCode_UserNotFoundException_test() throws Exception{
		
		when(branchService.doInactiveBranchByBranchCode(any(BranchRequest.class))).thenThrow(new UserNotFoundException(
				"The User was not found with given ID: " + mockedBranchRequestObject.getUserId(), "USER_NOT_FOUND"));
		
		ResultActions response =  mockMvc.perform(
				put("/pmu/v1/api/branch//inactive")
				.characterEncoding(Charset.defaultCharset())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockedBranchRequestObject))
			);
		
		response.andExpect(status().isNotFound())
		.andDo(print())
		.andExpect(jsonPath("$.message", is("The User was not found with given ID: " + mockedBranchRequestObject.getUserId())))
		.andExpect(jsonPath("$.errorCode", is("USER_NOT_FOUND")));
	}
	
	@Test
	public void getAllPMUServers_test() throws Exception{
		
		when(branchService.getAllPMUServers()).thenReturn(mockedBranchResponseObject.getPmuServers());
	
		ResultActions response =  mockMvc.perform(
				get("/pmu/v1/api/branch/get-pmu-servers")
		  );
		
		response.andExpect(status().isOk())
		.andDo(print())
		.andExpect(jsonPath("$.size()", is(2)));
	}
	
	@Test
	public void getAllMnemonic_test() throws Exception{
		
		when(branchService.getAllMnemonic()).thenReturn(List.of("AD", "GT"));
	
		ResultActions response =  mockMvc.perform(
				get("/pmu/v1/api/branch/get-allMnemonic")
		  );
		
		response.andExpect(status().isOk())
		.andDo(print())
		.andExpect(jsonPath("$.size()", is(2)));
	}
}
