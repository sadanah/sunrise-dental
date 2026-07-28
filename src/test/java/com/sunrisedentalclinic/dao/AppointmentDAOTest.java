package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Appointment;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentDAOTest {

    private AppointmentDAO appointmentDAO;

    @BeforeEach
    void setUp() {
        appointmentDAO = new AppointmentDAO();
        // sunrise_dental_test schema exists with seed data for known dentist/patient/treatment
    }

    @Test
    void findByDentistAndDateTime_returnsNullWhenSlotIsFree() {
        Appointment result = appointmentDAO.findByDentistAndDateTime("D001", LocalDate.of(2026, 8, 1), LocalTime.of(14, 0));
        assertNull(result);
    }

    @Test
    void save_thenFindById_returnsSameAppointment() {
        Appointment appt = new Appointment("APT-TEST-01", LocalDate.now().plusDays(2), LocalTime.of(9, 0));
        appt.setPatientID("P001");
        appt.setDentistID("D001");
        appt.setTreatmentID("T001");
        appt.setStaffID("R001");

        appointmentDAO.save(appt);
        Appointment found = appointmentDAO.findById("APT-TEST-01");
        assertNotNull(found);
        assertEquals("APT-TEST-01", found.getAppointmentNo());
    }
}
