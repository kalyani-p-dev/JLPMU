package com.jlp.pmu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jlp.pmu.models.User;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        user1 = new User();
        user1.setUserId("user123");
        user1.setStatus(true);
        // Set other properties for user1
       
        user2 = new User();
        user2.setUserId("user456");
        user2.setStatus(false);
        // Set other properties for user2
       
        // Save test data to the repository
        userRepository.saveAll(List.of(user1, user2));
    }

    @AfterEach
    void tearDown() {
        // Clear the repository after each test
        userRepository.deleteAll();
    }

    @Test
    void testFindBystatus() {
        // Test method findBystatus
        List<User> activeUsers = userRepository.findBystatus(true);
       
        assertNotNull(activeUsers);
        assertFalse(activeUsers.isEmpty());
        assertThat(activeUsers.size()).isEqualTo(1); // Assuming one user with status true
    }

    @Test
    void testFindByUserIdAndStatus() {
        // Test method findByUserIdAndStatus
        List<User> foundUsers = userRepository.findByUserIdAndStatus("user123", true);
       
        assertNotNull(foundUsers);
        assertFalse(foundUsers.isEmpty());
        assertThat(foundUsers.size()).isEqualTo(1); // Assuming one user with userId "user123" and status true
       
        User foundUser = foundUsers.get(0);
        assertThat(foundUser.getUserId()).isEqualTo("user123");
        assertThat(foundUser.getStatus()).isEqualTo(true);
    }

    // Add more test methods to cover other repository methods as needed
}