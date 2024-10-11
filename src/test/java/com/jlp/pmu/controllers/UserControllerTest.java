package com.jlp.pmu.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlp.pmu.config.TestConfig;
import com.jlp.pmu.dto.UserRequest;
import com.jlp.pmu.dto.UserResponse;
import com.jlp.pmu.enums.RoleType;
import com.jlp.pmu.enums.UserType;
import com.jlp.pmu.exception.UserAlreadyExistException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Roles;
import com.jlp.pmu.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import(TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRequest mockedUserRequest;
    private UserResponse mockedUserResponse;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockedUserRequest = UserRequest.builder()
                .userId("user123")
                .firstName("John")
                .lastName("Doe")
                .status(true)
                .location(List.of("Location1", "Location2"))
                .userType(UserType.INDIVIDUAL)
                .emailAddress("john.doe@example.com")
                .roles(List.of(RoleType.USER))
                .build();

        Roles role = new Roles();
        mockedUserResponse = new UserResponse();
        mockedUserResponse.setUserId("user123");
        mockedUserResponse.setFirstName("John");
        mockedUserResponse.setLastName("Doe");
        mockedUserResponse.setStatus(true);
        mockedUserResponse.setLocation(Set.of("Location1", "Location2"));
        mockedUserResponse.setUserType(UserType.INDIVIDUAL);
        mockedUserResponse.setEmailAddress("john.doe@example.com");
        mockedUserResponse.setRoles(Set.of(role));
    }

    @Test
    void testAddUserDetails_Success() throws Exception {
        when(userService.addUserDetails(any(UserRequest.class))).thenReturn(mockedUserResponse);

        mockMvc.perform(post("/pmu/v1/api/user/add-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    void testAddUserDetails_UserNotFoundException() throws Exception {
        when(userService.addUserDetails(any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("User not found with ID: " + mockedUserRequest.getUserId(), "USER_NOT_FOUND"));

        mockMvc.perform(post("/pmu/v1/api/user/add-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedUserRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: user123"))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }
    
    

//    @Test
//    void testUpdateUserDetails_Success() throws Exception {
//        when(userService.updateUserDetails(any(UserRequest.class))).thenReturn(mockedUserResponse);
//
//        mockMvc.perform(put("/pmu/v1/api/user/update-user")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(mockedUserRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value("user123"))
//                .andExpect(jsonPath("$.firstName").value("John"))
//                .andExpect(jsonPath("$.lastName").value("Doe"))
//                .andExpect(jsonPath("$.status").value(true))
//                .andExpect(jsonPath("$.location", containsInAnyOrder("Location1", "Location2")))
//
//
//                .andExpect(jsonPath("$.userType").value("INDIVIDUAL"))
//                .andExpect(jsonPath("$.emailAddress").value("john.doe@example.com"))
//                .andExpect(jsonPath("$.roles[0].roleName").value("ROLE_USER"));
//    }
    
    @Test
    void testUpdateUserDetails_Success() throws Exception {
        when(userService.updateUserDetails(any(UserRequest.class))).thenReturn(mockedUserResponse);

        MvcResult result = mockMvc.perform(put("/pmu/v1/api/user/update-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedUserRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Print the JSON response for debugging
        System.out.println(result.getResponse().getContentAsString());



        mockMvc.perform(put("/pmu/v1/api/user/update-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.location", containsInAnyOrder("Location1", "Location2")))
                .andExpect(jsonPath("$.userType").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.emailAddress").value("john.doe@example.com"));
                //.andExpect(jsonPath("$.roles[0].roleName").value("ROLE_USER"));
    }

    @Test
    void testUpdateUserDetails_UserNotFoundException() throws Exception {
        when(userService.updateUserDetails(any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("User not found with ID: " + mockedUserRequest.getUserId(), "USER_NOT_FOUND"));

        mockMvc.perform(put("/pmu/v1/api/user/update-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedUserRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: user123"))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }
    
    @Test
    void testGetAllUserDetails_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(mockedUserResponse);

        when(userService.getAllUserDeatils()).thenReturn(users);

        mockMvc.perform(get("/pmu/v1/api/user/get-all-user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user123"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].status").value(true))
                //.andExpect(jsonPath("$[0].location[0]").value("Location1"))
                .andExpect(jsonPath("$[0].location[1]").value("Location2"))
                .andExpect(jsonPath("$[0].userType").value("INDIVIDUAL"))
                .andExpect(jsonPath("$[0].emailAddress").value("john.doe@example.com"));
                //.andExpect(jsonPath("$[0].roles[0].roleName").value("ROLE_USER"));
    }
}
