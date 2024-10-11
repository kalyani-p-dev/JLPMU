package com.jlp.pmu.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jlp.pmu.enums.PrinterType;
import com.jlp.pmu.models.Printer;

@DataJpaTest
public class PrinterRepositoryTest {

    @Autowired
    private PrinterRepository printerRepository;

    private Printer printer1;
    private Printer printer2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        printer1 = new Printer();
        printer1.setPrinterName("Printer One");
        printer1.setBranchCode(1L);
        printer1.setPrinterType(PrinterType.LASER);
        printer1.setStatus(true);
        printer1.setLastUpdatedTime(LocalDateTime.now());

        printer2 = new Printer();
        printer2.setPrinterName("Printer Two");
        printer2.setBranchCode(2L); // Different branch code
        printer2.setPrinterType(PrinterType.LASER);
        printer2.setStatus(true);
        printer2.setLastUpdatedTime(LocalDateTime.now().minusDays(1));

        // Save test data to the repository
        printerRepository.saveAll(List.of(printer1, printer2));
    }

    @AfterEach
    void tearDown() {
        // Clear the repository after each test
        printerRepository.deleteAll();
    }

    @Test
    void testExistsByPrinterNameAndBranchCode() {
        // Test method existsByPrinterNameAndBranchCode
        boolean exists = printerRepository.existsByPrinterNameAndBranchCode("Printer One", 1L);
        assertTrue(exists);

        boolean notExists = printerRepository.existsByPrinterNameAndBranchCode("Nonexistent Printer", 1L);
        assertFalse(notExists);
    }

    @Test
    void testFindPrinterTypeByPrinterNameAndBranchCode() {
        // Test method findPrinterTypeByPrinterNameAndBranchCode
        PrinterType printerType = printerRepository.findPrinterTypeByPrinterNameAndBranchCode("Printer One", 1L);
        assertNotNull(printerType);
        assertThat(printerType).isEqualTo(PrinterType.LASER);
    }

    @Test
    void testFindByCombinedPrintTypeAndBranchCode() {
        // Test method findByCombinedPrintTypeAndBranchCode
        List<String> printerNames = printerRepository.findByCombinedPrintTypeAndBranchCode(true, PrinterType.LASER, 1L);
        assertNotNull(printerNames);
        assertFalse(printerNames.isEmpty());
        assertThat(printerNames.size()).isEqualTo(1);
        assertThat(printerNames).contains("Printer One");
    }

    @Test
    void testFindByBranchCodeAndPrinterName() {
        // Test method findByBranchCodeAndPrinterName
        Optional<Printer> foundPrinter = printerRepository.findByBranchCodeAndPrinterName(1L, "Printer One");
        assertNotNull(foundPrinter);
        assertTrue(foundPrinter.isPresent());
        assertThat(foundPrinter.get().getPrinterName()).isEqualTo("Printer One");
    }

    @Test
    void testFindByStatusAndBranchCodeOrderbydate() {
        // Test method findByStatusAndBranchCodeOrderbydate
        Printer printer3 = new Printer();
        printer3.setPrinterName("Printer Three");
        printer3.setBranchCode(1L);
        printer3.setPrinterType(PrinterType.LASER);
        printer3.setStatus(true);
        printer3.setLastUpdatedTime(LocalDateTime.now().minusDays(2));

        // Save additional test data
        printerRepository.save(printer3);

        List<Printer> printers = printerRepository.findByStatusAndBranchCodeOrderbydate(true, 1L);
        assertNotNull(printers);
        assertFalse(printers.isEmpty());
        assertThat(printers.size()).isEqualTo(2);
        assertThat(printers.get(0).getPrinterName()).isEqualTo("Printer One");
        assertThat(printers.get(1).getPrinterName()).isEqualTo("Printer Three");
    }
}