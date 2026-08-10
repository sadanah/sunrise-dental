package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.TreatmentType;
import com.sunrisedentalclinic.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentTypeDAO implements IDAO<TreatmentType> {

    @Override
    public void save(TreatmentType treatmentType) {
        String sql = "INSERT INTO treatment_type (treatmentID, treatmentName, baseCost) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, treatmentType.getTreatmentID());
            stmt.setString(2, treatmentType.getTreatmentName());
            stmt.setBigDecimal(3, treatmentType.getBaseCost());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving treatment type", e);
        }
    }

    @Override
    public TreatmentType findById(String treatmentID) {
        String sql = "SELECT * FROM treatment_type WHERE treatmentID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, treatmentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding treatment type", e);
        }
    }

    @Override
    public List<TreatmentType> findAll() {
        List<TreatmentType> types = new ArrayList<>();
        String sql = "SELECT * FROM treatment_type";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                types.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving treatment types", e);
        }
        return types;
    }

    @Override
    public void update(TreatmentType treatmentType) {
        String sql = "UPDATE treatment_type SET treatmentName=?, baseCost=? WHERE treatmentID=?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, treatmentType.getTreatmentName());
            stmt.setBigDecimal(2, treatmentType.getBaseCost());
            stmt.setString(3, treatmentType.getTreatmentID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating treatment type", e);
        }
    }

    @Override
    public void delete(String treatmentID) {
        String sql = "DELETE FROM treatment_type WHERE treatmentID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, treatmentID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting treatment type", e);
        }
    }

    private TreatmentType mapRow(ResultSet rs) throws SQLException {
        return new TreatmentType(
                rs.getString("treatmentID"),
                rs.getString("treatmentName"),
                rs.getBigDecimal("baseCost")
        );
    }
}
