
package com.jlp.pmu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jlp.pmu.models.Template;

@DataJpaTest
public class TemplateRepositoryTest {

    @Autowired
    private TemplateRepository templateRepository;

    private Template template1;
    private Template template2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        template1 = new Template();
        template1.setTemplateName("Template One");
        template1.setTemplateDescription("Description for Template One");
        template1.setDateTime(LocalDateTime.now());
        template1.setFileSIze(12345L);
        template1.setCreatedBy("User1");
        template1.setUpdatedBy("User1");
        template1.setStatus(true);
        template1.setLastupdatedtime(LocalDateTime.now());
        template1.setComment("Initial Comment");

        template2 = new Template();
        template2.setTemplateName("Template Two");
        template2.setTemplateDescription("Description for Template Two");
        template2.setDateTime(LocalDateTime.now());
        template2.setFileSIze(67890L);
        template2.setCreatedBy("User2");
        template2.setUpdatedBy("User2");
        template2.setStatus(true);
        template2.setLastupdatedtime(LocalDateTime.now());
        template2.setComment("Initial Comment");

        // Save test data to the repository
        templateRepository.saveAll(List.of(template1, template2));
    }

    @AfterEach
    void tearDown() {
        // Clear the repository after each test
        templateRepository.deleteAll();
    }

    @Test
    void testFindById() {
        // Test method findById
        Optional<Template> foundTemplate = templateRepository.findById(template1.getTempId());

        assertNotNull(foundTemplate);
        assertThat(foundTemplate.isPresent()).isTrue();
        assertThat(foundTemplate.get().getTemplateName()).isEqualTo("Template One");
        assertThat(foundTemplate.get().getTemplateDescription()).isEqualTo("Description for Template One");
    }

    @Test
    void testFindById_NotFound() {
        // Test method findById with non-existent ID
        Optional<Template> foundTemplate = templateRepository.findById(999);

        assertNotNull(foundTemplate);
        assertThat(foundTemplate.isEmpty()).isTrue();
    }

    // Add more test methods to cover other repository methods as needed
}