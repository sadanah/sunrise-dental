package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.*;
import com.sunrisedentalclinic.util.DBConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO implements IDAO<Staff> {

    @Override
    public void save(Staff staff) {
        String sql = "INSERT INTO staff (name, contactNo, address, staffID, username, passwordHash, role, specialization, consultationFee, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getContactNo());
            stmt.setString(3, staff.getAddress());
            stmt.setString(4, staff.getStaffID());
            stmt.setString(5, staff.getUsername());
            stmt.setString(6, staff.getPasswordHash());
            stmt.setString(7, staff.getRole());

            if (staff instanceof Dentist dentist) {
                stmt.setString(8, dentist.getSpecialization());
                stmt.setBigDecimal(9, dentist.getConsultationFee());
            } else {
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.DECIMAL);
            }
            stmt.setString(10, staff.getEmail());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving staff", e);
        }
    }

    @Override
    public Staff findById(String staffID) {
        String sql = "SELECT * FROM staff WHERE staffID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding staff", e);
        }
    }

    public Staff findByUsername(String username) {
        String sql = "SELECT * FROM staff WHERE username = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding staff by username", e);
        }
    }

    @Override
    public List<Staff> findAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                staffList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving staff", e);
        }
        return staffList;
    }

    @Override
    public void update(Staff staff) {
        String sql = "UPDATE staff SET name=?, contactNo=?, address=?, username=?, passwordHash=?, specialization=?, consultationFee=?, email=? WHERE staffID=?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getName());
            stmt.setString(2, staff.getContactNo());
            stmt.setString(3, staff.getAddress());
            stmt.setString(4, staff.getUsername());
            stmt.setString(5, staff.getPasswordHash());

            if (staff instanceof Dentist dentist) {
                stmt.setString(6, dentist.getSpecialization());
                stmt.setBigDecimal(7, dentist.getConsultationFee());
            } else {
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.DECIMAL);
            }
            stmt.setString(8, staff.getEmail());
            stmt.setString(9, staff.getStaffID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating staff", e);
        }
    }

    @Override
    public void delete(String staffID) {
        String sql = "DELETE FROM staff WHERE staffID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting staff", e);
        }
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        int personID = rs.getInt("personID");
        String name = rs.getString("name");
        String contactNo = rs.getString("contactNo");
        String address = rs.getString("address");
        String staffID = rs.getString("staffID");
        String username = rs.getString("username");
        String passwordHash = rs.getString("passwordHash");

        Staff staff;
        switch (role) {
            case "DENTIST":
                staff = new Dentist(personID, name, contactNo, address, staffID, username, passwordHash,
                        rs.getString("specialization"), rs.getBigDecimal("consultationFee"));
                break;
            case "RECEPTIONIST":
                staff = new Receptionist(personID, name, contactNo, address, staffID, username, passwordHash);
                break;
            case "ADMIN":
                staff = new Admin(personID, name, contactNo, address, staffID, username, passwordHash);
                break;
            default:
                throw new IllegalStateException("Unknown staff role: " + role);
        }
        staff.setEmail(rs.getString("email"));
        return staff;
    }
}