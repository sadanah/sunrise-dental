package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Dentist;
import com.sunrisedentalclinic.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO implements IDAO<Dentist> {

    @Override
    public void save(Dentist dentist) {
        String sql = "INSERT INTO staff (name, contactNo, address, staffID, username, passwordHash, role, specialization, consultationFee) VALUES (?, ?, ?, ?, ?, ?, 'DENTIST', ?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dentist.getName());
            stmt.setString(2, dentist.getContactNo());
            stmt.setString(3, dentist.getAddress());
            stmt.setString(4, dentist.getStaffID());
            stmt.setString(5, dentist.getUsername());
            stmt.setString(6, dentist.getPasswordHash());
            stmt.setString(7, dentist.getSpecialization());
            stmt.setBigDecimal(8, dentist.getConsultationFee());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving dentist", e);
        }
    }

    @Override
    public Dentist findById(String staffID) {
        String sql = "SELECT * FROM staff WHERE staffID = ? AND role = 'DENTIST'";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding dentist", e);
        }
    }

    @Override
    public List<Dentist> findAll() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE role = 'DENTIST'";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dentists.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dentists", e);
        }
        return dentists;
    }

    @Override
    public void update(Dentist dentist) {
        String sql = "UPDATE staff SET name=?, contactNo=?, address=?, username=?, passwordHash=?, specialization=?, consultationFee=? WHERE staffID=?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dentist.getName());
            stmt.setString(2, dentist.getContactNo());
            stmt.setString(3, dentist.getAddress());
            stmt.setString(4, dentist.getUsername());
            stmt.setString(5, dentist.getPasswordHash());
            stmt.setString(6, dentist.getSpecialization());
            stmt.setBigDecimal(7, dentist.getConsultationFee());
            stmt.setString(8, dentist.getStaffID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dentist", e);
        }
    }

    @Override
    public void delete(String staffID) {
        String sql = "DELETE FROM staff WHERE staffID = ? AND role = 'DENTIST'";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting dentist", e);
        }
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        return new Dentist(
                rs.getInt("personID"),
                rs.getString("name"),
                rs.getString("contactNo"),
                rs.getString("address"),
                rs.getString("staffID"),
                rs.getString("username"),
                rs.getString("passwordHash"),
                rs.getString("specialization"),
                rs.getBigDecimal("consultationFee")
        );
    }
}
