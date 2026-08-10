package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Receptionist;
import com.sunrisedentalclinic.domain.Staff;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class StaffDAOTest {

    private StaffDAO staffDAO;

    @BeforeEach
    void setUp() {
        staffDAO = new StaffDAO();
    }

    @AfterEach
    void tearDown() {
        staffDAO.delete("STF-TEST-01");
    }

    @Test
    void save_thenFindById_returnsSameStaff() {
        Staff staff = new Receptionist(0, "Kasun Silva", "0771234567", "Colombo",
                "STF-TEST-01", "ksilva-test", "hashed-pw");

        staffDAO.save(staff);
        Staff found = staffDAO.findById("STF-TEST-01");

        assertNotNull(found);
        assertEquals("Kasun Silva", found.getName());
        assertEquals("RECEPTIONIST", found.getRole());
    }

    @Test
    void findById_returnsNullWhenNotFound() {
        Staff result = staffDAO.findById("STF-DOES-NOT-EXIST");
        assertNull(result);
    }

    @Test
    void findByUsername_returnsMatchingStaff() {
        Staff staff = new Receptionist(0, "Nadeeka Perera", "0779998887", "Galle",
                "STF-TEST-01", "nperera-test", "hashed-pw");
        staffDAO.save(staff);

        Staff found = staffDAO.findByUsername("nperera-test");

        assertNotNull(found);
        assertEquals("STF-TEST-01", found.getStaffID());
    }

    @Test
    void update_changesNameAndContact() {
        Staff staff = new Receptionist(0, "Old Name", "0770000000", "Old Address",
                "STF-TEST-01", "oldname-test", "hashed-pw");
        staffDAO.save(staff);

        Staff toUpdate = staffDAO.findById("STF-TEST-01");
        toUpdate.setName("New Name");
        toUpdate.setContactNo("0771112223");
        staffDAO.update(toUpdate);

        Staff updated = staffDAO.findById("STF-TEST-01");
        assertEquals("New Name", updated.getName());
        assertEquals("0771112223", updated.getContactNo());
    }

    @Test
    void delete_removesRecord() {
        Staff staff = new Receptionist(0, "To Delete", "0771234567", "Matara",
                "STF-TEST-01", "todelete-test", "hashed-pw");
        staffDAO.save(staff);
        assertNotNull(staffDAO.findById("STF-TEST-01"));

        staffDAO.delete("STF-TEST-01");

        assertNull(staffDAO.findById("STF-TEST-01"));
    }
}