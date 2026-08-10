package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Bill;
import com.sunrisedentalclinic.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillDAO implements IDAO<Bill> {

    @Override
    public void save(Bill bill) {
        String sql = "INSERT INTO bill (billID, appointmentNo, consultationFee, treatmentCost, totalAmount, generatedDate) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bill.getBillID());
            stmt.setString(2, bill.getAppointmentNo());
            stmt.setBigDecimal(3, bill.getConsultationFee());
            stmt.setBigDecimal(4, bill.getTreatmentCost());
            stmt.setBigDecimal(5, bill.getTotalAmount());
            stmt.setTimestamp(6, Timestamp.valueOf(bill.getGeneratedDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving bill", e);
        }
    }

    @Override
    public Bill findById(String billID) {
        String sql = "SELECT * FROM bill WHERE billID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill", e);
        }
    }

    public List<Bill> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.* FROM bill b JOIN appointment a ON b.appointmentNo = a.appointmentNo " +
                "WHERE a.appointmentDate BETWEEN ? AND ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bills.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving bills by date range", e);
        }
        return bills;
    }

    @Override
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bill";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bills.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving bills", e);
        }
        return bills;
    }

    @Override
    public void update(Bill bill) {
        String sql = "UPDATE bill SET consultationFee=?, treatmentCost=?, totalAmount=? WHERE billID=?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, bill.getConsultationFee());
            stmt.setBigDecimal(2, bill.getTreatmentCost());
            stmt.setBigDecimal(3, bill.getTotalAmount());
            stmt.setString(4, bill.getBillID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating bill", e);
        }
    }

    @Override
    public void delete(String billID) {
        String sql = "DELETE FROM bill WHERE billID = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, billID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting bill", e);
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill bill = new Bill(
                rs.getString("billID"),
                rs.getBigDecimal("consultationFee"),
                rs.getBigDecimal("treatmentCost")
        );
        bill.setAppointmentNo(rs.getString("appointmentNo"));
        bill.overrideTotalAmount(rs.getBigDecimal("totalAmount"));
        return bill;
    }
}
