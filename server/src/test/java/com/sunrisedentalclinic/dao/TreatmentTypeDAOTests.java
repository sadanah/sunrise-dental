package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.TreatmentType;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class TreatmentTypeDAOTest {

    private TreatmentTypeDAO treatmentTypeDAO;

    @BeforeEach
    void setUp() {
        treatmentTypeDAO = new TreatmentTypeDAO();
    }

    @AfterEach
    void tearDown() {
        treatmentTypeDAO.delete("TRT-TEST-01");
    }

    @Test
    void save_thenFindById_returnsSameTreatmentType() {
        TreatmentType treatment = new TreatmentType("TRT-TEST-01", "Test Cleaning", new BigDecimal("3500.00"));

        treatmentTypeDAO.save(treatment);
        TreatmentType found = treatmentTypeDAO.findById("TRT-TEST-01");

        assertNotNull(found);
        assertEquals("Test Cleaning", found.getTreatmentName());
        assertEquals(new BigDecimal("3500.00"), found.getBaseCost());
    }

    @Test
    void findById_returnsNullWhenNotFound() {
        TreatmentType result = treatmentTypeDAO.findById("TRT-DOES-NOT-EXIST");
        assertNull(result);
    }

    @Test
    void update_changesNameAndBaseCost() {
        TreatmentType treatment = new TreatmentType("TRT-TEST-01", "Old Name", new BigDecimal("1000.00"));
        treatmentTypeDAO.save(treatment);

        TreatmentType toUpdate = treatmentTypeDAO.findById("TRT-TEST-01");
        toUpdate.setTreatmentName("New Name");
        toUpdate.setBaseCost(new BigDecimal("1500.00"));
        treatmentTypeDAO.update(toUpdate);

        TreatmentType updated = treatmentTypeDAO.findById("TRT-TEST-01");
        assertEquals("New Name", updated.getTreatmentName());
        assertEquals(new BigDecimal("1500.00"), updated.getBaseCost());
    }

    @Test
    void delete_removesRecord() {
        TreatmentType treatment = new TreatmentType("TRT-TEST-01", "To Delete", new BigDecimal("2000.00"));
        treatmentTypeDAO.save(treatment);
        assertNotNull(treatmentTypeDAO.findById("TRT-TEST-01"));

        treatmentTypeDAO.delete("TRT-TEST-01");

        assertNull(treatmentTypeDAO.findById("TRT-TEST-01"));
    }

    @Test
    void findAll_includesSavedTreatmentType() {
        TreatmentType treatment = new TreatmentType("TRT-TEST-01", "Findable Treatment", new BigDecimal("2500.00"));
        treatmentTypeDAO.save(treatment);

        boolean found = treatmentTypeDAO.findAll().stream()
                .anyMatch(t -> "TRT-TEST-01".equals(t.getTreatmentID()));

        assertTrue(found);
    }
}