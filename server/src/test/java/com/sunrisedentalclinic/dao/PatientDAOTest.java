package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Patient;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PatientDAOTest {

    private PatientDAO patientDAO;

    @BeforeEach
    void setUp() {
        patientDAO = new PatientDAO();
    }

    @AfterEach
    void tearDown() {
        patientDAO.delete("PAT-TEST-01");
    }

    @Test
    void save_thenFindById_returnsSamePatient() {
        Patient patient = new Patient(0, "Jane Doe", "0771234567", "Colombo", "PAT-TEST-01", LocalDate.now());

        patientDAO.save(patient);
        Patient found = patientDAO.findById("PAT-TEST-01");

        assertNotNull(found);
        assertEquals("Jane Doe", found.getName());
        assertEquals("0771234567", found.getContactNo());
        assertEquals("Colombo", found.getAddress());
    }

    @Test
    void findById_returnsNullWhenNotFound() {
        Patient result = patientDAO.findById("PAT-DOES-NOT-EXIST");
        assertNull(result);
    }

    @Test
    void update_changesNameContactAndAddress() {
        Patient patient = new Patient(0, "Old Name", "0770000000", "Old Address", "PAT-TEST-01", LocalDate.now());
        patientDAO.save(patient);

        Patient toUpdate = patientDAO.findById("PAT-TEST-01");
        toUpdate.setName("New Name");
        toUpdate.setContactNo("0779998887");
        toUpdate.setAddress("New Address");
        patientDAO.update(toUpdate);

        Patient updated = patientDAO.findById("PAT-TEST-01");
        assertEquals("New Name", updated.getName());
        assertEquals("0779998887", updated.getContactNo());
        assertEquals("New Address", updated.getAddress());
    }

    @Test
    void delete_removesRecord() {
        Patient patient = new Patient(0, "To Delete", "0771112223", "Galle", "PAT-TEST-01", LocalDate.now());
        patientDAO.save(patient);
        assertNotNull(patientDAO.findById("PAT-TEST-01"));

        patientDAO.delete("PAT-TEST-01");

        assertNull(patientDAO.findById("PAT-TEST-01"));
    }

    @Test
    void findAll_includesSavedPatient() {
        Patient patient = new Patient(0, "Findable Patient", "0771234567", "Kandy", "PAT-TEST-01", LocalDate.now());
        patientDAO.save(patient);

        boolean found = patientDAO.findAll().stream()
                .anyMatch(p -> "PAT-TEST-01".equals(p.getPatientID()));

        assertTrue(found);
    }
}