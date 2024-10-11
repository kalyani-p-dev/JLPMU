
package com.jlp.pmu.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.jlp.pmu.dto.UserRequest;
import com.jlp.pmu.dto.UserResponse;
import com.jlp.pmu.enums.RoleType;
import com.jlp.pmu.enums.UserType;
import com.jlp.pmu.exception.UserAlreadyExistException;
import com.jlp.pmu.exception.UserNotFoundException;
import com.jlp.pmu.models.Branch;
import com.jlp.pmu.models.Roles;
import com.jlp.pmu.models.User;
import com.jlp.pmu.repository.ActivityRepository;
import com.jlp.pmu.repository.BranchRepository;
import com.jlp.pmu.repository.RoleRepository;
import com.jlp.pmu.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserRequest mockedUserRequest;
    private User mockedUser;
    private UserResponse mockedUserResponse;
    private Branch mockedBranch;
    private Roles mockedRole;

    @BeforeEach
    void setUp() {
        mockedUserRequest = UserRequest.builder()
            .userId("A12345")
            .firstName("Alice")
            .lastName("Smith")
            .status(true)
            .location(Arrays.asList("NY"))
            .userType(UserType.INDIVIDUAL)
            .emailAddress("alice.smith@example.com")
            .roles(Arrays.asList(RoleType.USER))
            .build();

        mockedUser = new User();
        mockedUser.setUserId("A12345");
        mockedUser.setFirstName("Alice");
        mockedUser.setLastName("Smith");
        mockedUser.setStatus(true);
        mockedUser.setEmailAddress("alice.smith@example.com");
        mockedUser.setUserType(UserType.INDIVIDUAL);
        mockedUser.setCreatedBy("Alice Smith");
        mockedUser.setUpdatedBy("Alice Smith");
        mockedUser.setLastUpdatedTime(LocalDateTime.now());
        mockedUser.setComment("added By Alice Smith");

        mockedUserResponse = new UserResponse();
        mockedUserResponse.setUserId("A12345");
        mockedUserResponse.setFirstName("Alice");
        mockedUserResponse.setLastName("Smith");
        mockedUserResponse.setEmailAddress("alice.smith@example.com");
        mockedUserResponse.setLocation(new HashSet<>(Arrays.asList("NY")));

        mockedBranch = new Branch();
        mockedBranch.setBranchCode(101L);
        mockedBranch.setMnemonic("NY");
       
        mockedRole = new Roles();
        mockedRole.setRoleTypes(RoleType.USER);
    }

    @Test
    void addUserDetails_success_test() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        when(branchRepository.findByMnemonic(anyString())).thenReturn(Optional.of(mockedBranch));
        when(roleRepository.findByRoleTypes(any(RoleType.class))).thenReturn(Optional.of(mockedRole));
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);
        when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(mockedUserResponse);

        UserResponse response = userServiceImpl.addUserDetails(mockedUserRequest);

        assertNotNull(response);
        assertEquals("A12345", response.getUserId());
        assertEquals("Alice", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("alice.smith@example.com", response.getEmailAddress());
        assertTrue(response.getLocation().contains("NY"));

        verify(userRepository, times(1)).findById(anyString());
        verify(branchRepository, times(1)).findByMnemonic(anyString());
        verify(roleRepository, times(1)).findByRoleTypes(any(RoleType.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(modelMapper, times(1)).map(any(User.class), any(Class.class));
    }

    @Test
    void addUserDetails_UserAlreadyExistException_test() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(mockedUser));

        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> {
            userServiceImpl.addUserDetails(mockedUserRequest);
        });

        assertEquals("USER_ALREADY_EXISTS", exception.getErrorCode());
        assertEquals("User Already Exist with given UserId: A12345", exception.getMessage());

        verify(userRepository, times(1)).findById(anyString());
        verify(branchRepository, never()).findByMnemonic(anyString());
        verify(roleRepository, never()).findByRoleTypes(any(RoleType.class));
        verify(userRepository, never()).save(any(User.class));
        verify(modelMapper, never()).map(any(User.class), any(Class.class));
    }
    
    @Test
    void updateUserDetails_success_test() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(mockedUser));
        when(branchRepository.findByMnemonic(anyString())).thenReturn(Optional.of(mockedBranch));
        when(roleRepository.findByRoleTypes(any(RoleType.class))).thenReturn(Optional.of(mockedRole));
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);
        when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(mockedUserResponse);

        UserResponse response = userServiceImpl.updateUserDetails(mockedUserRequest);



        assertNotNull(response);
        assertEquals("A12345", response.getUserId());
        assertEquals("Alice", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("alice.smith@example.com", response.getEmailAddress());
        assertTrue(response.getLocation().contains("NY"));

        verify(userRepository, times(1)).findById(anyString());
        verify(branchRepository, times(1)).findByMnemonic(anyString());
        verify(roleRepository, times(1)).findByRoleTypes(any(RoleType.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(modelMapper, times(1)).map(any(User.class), any(Class.class));
    }
    
    @Test
    void updateUserDetails_UserNotFoundException_test() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userServiceImpl.updateUserDetails(mockedUserRequest);
        });

        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        assertEquals("The User was not found with given ID: A12345", exception.getMessage());

        verify(userRepository, times(1)).findById(anyString());
        verify(branchRepository, never()).findByMnemonic(anyString());
        verify(roleRepository, never()).findByRoleTypes(any(RoleType.class));
        verify(userRepository, never()).save(any(User.class));
        verify(modelMapper, never()).map(any(User.class), any(Class.class));
    }

//    @Test
//    void getAllUserDetails_success_test() {
//        List<User> users = Arrays.asList(mockedUser);
//        when(userRepository.findAll()).thenReturn(users);
//        when(branchRepository.findByBranchStatus(true)).thenReturn(Arrays.asList(mockedBranch));
//        when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(mockedUserResponse);
//
//        mockedUser.setPresentInAllBranches(true);
//        List<UserResponse> response = userServiceImpl.getAllUserDeatils();
//
//        assertNotNull(response);
//        assertEquals(1, response.size());
//        assertEquals("A12345", response.get(0).getUserId());
//
//        verify(userRepository, times(1)).findAll();
//        verify(branchRepository, times(1)).findByBranchStatus(true);
//        verify(modelMapper, times(1)).map(any(User.class), any(Class.class));
//    }
}

