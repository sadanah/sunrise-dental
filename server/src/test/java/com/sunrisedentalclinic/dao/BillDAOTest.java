package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.Bill;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class BillDAOTest {

    private BillDAO billDAO;
    private AppointmentDAO appointmentDAO;

    @BeforeEach
    void setUp() {
        billDAO = new BillDAO();
        appointmentDAO = new AppointmentDAO();

        // Bill has a foreign key on appointmentNo — create a real parent appointment
        // first so save() doesn't depend on stray data existing elsewhere.
        Appointment appt = new Appointment("APT-BILL-TEST-01", LocalDate.now().plusDays(1), LocalTime.of(11, 0));
        appt.setPatientID("P001");
        appt.setDentistID("D001");
        appt.setTreatmentID("T001");
        appt.setStaffID("R001");
        appointmentDAO.save(appt);
    }

    @AfterEach
    void tearDown() {
        billDAO.delete("BILL-TEST-01");
        appointmentDAO.delete("APT-BILL-TEST-01");
    }

    @Test
    void save_thenFindById_returnsSameBill() {
        Bill bill = new Bill("BILL-TEST-01", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        bill.setAppointmentNo("APT-BILL-TEST-01");

        billDAO.save(bill);
        Bill found = billDAO.findById("BILL-TEST-01");

        assertNotNull(found);
        assertEquals("APT-BILL-TEST-01", found.getAppointmentNo());
        assertEquals(new BigDecimal("6000.00"), found.getTotalAmount());
    }

    @Test
    void findById_returnsNullWhenNotFound() {
        Bill result = billDAO.findById("BILL-DOES-NOT-EXIST");
        assertNull(result);
    }

    @Test
    void findByDateRange_includesMatchingBill() {
        Bill bill = new Bill("BILL-TEST-01", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        bill.setAppointmentNo("APT-BILL-TEST-01");
        billDAO.save(bill);

        List<Bill> results = billDAO.findByDateRange(LocalDate.now(), LocalDate.now().plusDays(2));

        boolean found = results.stream().anyMatch(b -> "BILL-TEST-01".equals(b.getBillID()));
        assertTrue(found);
    }

    @Test
    void findByDateRange_excludesBillOutsideRange() {
        Bill bill = new Bill("BILL-TEST-01", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        bill.setAppointmentNo("APT-BILL-TEST-01");
        billDAO.save(bill);

        // The parent appointment is tomorrow — a range entirely in the past should exclude it
        List<Bill> results = billDAO.findByDateRange(LocalDate.now().minusDays(30), LocalDate.now().minusDays(10));

        boolean found = results.stream().anyMatch(b -> "BILL-TEST-01".equals(b.getBillID()));
        assertFalse(found);
    }

    @Test
    void update_changesTotalAmount() {
        Bill bill = new Bill("BILL-TEST-01", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        bill.setAppointmentNo("APT-BILL-TEST-01");
        billDAO.save(bill);

        Bill toUpdate = billDAO.findById("BILL-TEST-01");
        toUpdate.overrideTotalAmount(new BigDecimal("5400.00"));
        billDAO.update(toUpdate);

        Bill updated = billDAO.findById("BILL-TEST-01");
        assertEquals(new BigDecimal("5400.00"), updated.getTotalAmount());
    }

    @Test
    void delete_removesRecord() {
        Bill bill = new Bill("BILL-TEST-01", new BigDecimal("2500.00"), new BigDecimal("3500.00"));
        bill.setAppointmentNo("APT-BILL-TEST-01");
        billDAO.save(bill);
        assertNotNull(billDAO.findById("BILL-TEST-01"));

        billDAO.delete("BILL-TEST-01");

        assertNull(billDAO.findById("BILL-TEST-01"));
    }
}