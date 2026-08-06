package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Dentist;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class DentistDAOTest {

    private DentistDAO dentistDAO;

    @BeforeEach
    void setUp() {
        dentistDAO = new DentistDAO();
    }

    @AfterEach
    void tearDown() {
        dentistDAO.delete("DEN-TEST-01");
    }

    @Test
    void save_thenFindById_returnsSameDentist() {
        Dentist dentist = new Dentist(0, "Dr. Fernando", "0771234567", "Colombo",
                "DEN-TEST-01", "dfernando-test", "hashed-pw", "Orthodontics", new BigDecimal("3000.00"));

        dentistDAO.save(dentist);
        Dentist found = dentistDAO.findById("DEN-TEST-01");

        assertNotNull(found);
        assertEquals("Dr. Fernando", found.getName());
        assertEquals("Orthodontics", found.getSpecialization());
        assertEquals(new BigDecimal("3000.00"), found.getConsultationFee());
    }

    @Test
    void findById_returnsNullWhenNotFound() {
        Dentist result = dentistDAO.findById("DEN-DOES-NOT-EXIST");
        assertNull(result);
    }

    @Test
    void findById_returnsNullForNonDentistStaffID() {
        // findById filters WHERE role = 'DENTIST' — confirms it won't accidentally
        // return a Receptionist/Admin row that happens to share a staffID pattern.
        // Uses a StaffID that's guaranteed not to exist as any role.
        Dentist result = dentistDAO.findById("STF-DOES-NOT-EXIST-EITHER");
        assertNull(result);
    }

    @Test
    void update_changesSpecializationAndFee() {
        Dentist dentist = new Dentist(0, "Dr. Old", "0770000000", "Old Address",
                "DEN-TEST-01", "drold-test", "hashed-pw", "General", new BigDecimal("2000.00"));
        dentistDAO.save(dentist);

        Dentist toUpdate = dentistDAO.findById("DEN-TEST-01");
        toUpdate.setSpecialization("Periodontics");
        toUpdate.setConsultationFee(new BigDecimal("3500.00"));
        dentistDAO.update(toUpdate);

        Dentist updated = dentistDAO.findById("DEN-TEST-01");
        assertEquals("Periodontics", updated.getSpecialization());
        assertEquals(new BigDecimal("3500.00"), updated.getConsultationFee());
    }

    @Test
    void delete_removesRecord() {
        Dentist dentist = new Dentist(0, "To Delete", "0771234567", "Matara",
                "DEN-TEST-01", "todelete-test", "hashed-pw", "General", new BigDecimal("2500.00"));
        dentistDAO.save(dentist);
        assertNotNull(dentistDAO.findById("DEN-TEST-01"));

        dentistDAO.delete("DEN-TEST-01");

        assertNull(dentistDAO.findById("DEN-TEST-01"));
    }

    @Test
    void findAll_includesSavedDentist() {
        Dentist dentist = new Dentist(0, "Findable Dentist", "0771234567", "Kandy",
                "DEN-TEST-01", "findable-test", "hashed-pw", "General", new BigDecimal("2500.00"));
        dentistDAO.save(dentist);

        boolean found = dentistDAO.findAll().stream()
                .anyMatch(d -> "DEN-TEST-01".equals(d.getStaffID()));

        assertTrue(found);
    }
}