package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Patient;
import com.sunrisedentalclinic.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO implements IDAO<Patient> {

    @Override
    public void save(Patient patient) {
        String sql = "INSERT INTO patient (name, contactNo, address, patientID, registeredDate, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getContactNo());
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getPatientID());
            stmt.setDate(5, Date.valueOf(patient.getRegisteredDate()));
            stmt.setString(6, patient.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving patient", e);
        }
    }

    @Override
    public Patient findById(String patientID) {
        String sql = "SELECT * FROM patient WHERE patientID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding patient", e);
        }
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patient";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving patients", e);
        }
        return patients;
    }

    @Override
    public void update(Patient patient) {
        String sql = "UPDATE patient SET name=?, contactNo=?, address=?, email=? WHERE patientID=?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getContactNo());
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getEmail());
            stmt.setString(5, patient.getPatientID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating patient", e);
        }
    }

    @Override
    public void delete(String patientID) {
        String sql = "DELETE FROM patient WHERE patientID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting patient", e);
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getInt("personID"),
                rs.getString("name"),
                rs.getString("contactNo"),
                rs.getString("address"),
                rs.getString("patientID"),
                rs.getDate("registeredDate").toLocalDate()
        );
        patient.setEmail(rs.getString("email"));
        return patient;
    }
}
