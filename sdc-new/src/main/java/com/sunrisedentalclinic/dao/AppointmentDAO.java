package com.sunrisedentalclinic.dao;

import com.sunrisedentalclinic.domain.Appointment;
import com.sunrisedentalclinic.domain.AppointmentStatus;
import com.sunrisedentalclinic.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO implements IDAO<Appointment> {

    @Override
    public void save(Appointment appointment) {
        String sql = "INSERT INTO appointment " +
                "(appointmentNo, patientID, dentistID, treatmentID, staffID, " +
                "appointmentDate, appointmentTime, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getAppointmentNo());
            stmt.setString(2, appointment.getPatientID());
            stmt.setString(3, appointment.getDentistID());
            stmt.setString(4, appointment.getTreatmentID());
            stmt.setString(5, appointment.getStaffID());
            stmt.setDate(6, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(7, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setString(8, appointment.getStatus().name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving appointment", e);
        }
    }

    @Override
    public Appointment findById(String appointmentNo) {
        String sql = "SELECT * FROM appointment WHERE appointmentNo = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentNo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding appointment", e);
        }
    }

    public Appointment findByDentistAndDateTime(
            String dentistID,
            LocalDate date,
            LocalTime time) {

        String sql = "SELECT * FROM appointment " +
                "WHERE dentistID = ? " +
                "AND appointmentDate = ? " +
                "AND appointmentTime = ? " +
                "AND status = 'SCHEDULED'";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentistID);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error checking appointment availability", e);
        }
    }

    public List<Appointment> findByPatient(String patientID) {
        List<Appointment> appointments = new ArrayList<>();

        String sql = "SELECT * FROM appointment WHERE patientID = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding appointments for patient", e);
        }

        return appointments;
    }

    /**
     * Finds all appointments scheduled for a specific date.
     */
    public List<Appointment> findByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();

        String sql = "SELECT * FROM appointment " +
                "WHERE appointmentDate = ? " +
                "ORDER BY appointmentTime";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding appointments by date", e);
        }

        return appointments;
    }

    /**
     * Finds all appointments assigned to a specific dentist.
     */
    public List<Appointment> findByDentist(String dentistID) {
        List<Appointment> appointments = new ArrayList<>();

        String sql = "SELECT * FROM appointment " +
                "WHERE dentistID = ? " +
                "ORDER BY appointmentDate, appointmentTime";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentistID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding appointments for dentist", e);
        }

        return appointments;
    }

    /**
     * Finds all appointments for a specific dentist on a specific date.
     */
    public List<Appointment> findByDentistAndDate(String dentistID, LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();

        String sql = "SELECT * FROM appointment " +
                "WHERE dentistID = ? AND appointmentDate = ? " +
                "ORDER BY appointmentTime";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentistID);
            stmt.setDate(2, Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding appointments for dentist on date", e);
        }

        return appointments;
    }

    @Override
    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();

        String sql = "SELECT * FROM appointment";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                appointments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error retrieving appointments", e);
        }

        return appointments;
    }

    @Override
    public void update(Appointment appointment) {
        String sql = "UPDATE appointment SET status = ? " +
                "WHERE appointmentNo = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getStatus().name());
            stmt.setString(2, appointment.getAppointmentNo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error updating appointment", e);
        }
    }

    @Override
    public void delete(String appointmentNo) {
        String sql = "DELETE FROM appointment WHERE appointmentNo = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentNo);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error deleting appointment", e);
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment(
                rs.getString("appointmentNo"),
                rs.getDate("appointmentDate").toLocalDate(),
                rs.getTime("appointmentTime").toLocalTime()
        );

        appointment.setPatientID(rs.getString("patientID"));
        appointment.setDentistID(rs.getString("dentistID"));
        appointment.setTreatmentID(rs.getString("treatmentID"));
        appointment.setStaffID(rs.getString("staffID"));

        appointment.updateStatusFromDB(
                AppointmentStatus.valueOf(rs.getString("status"))
        );

        return appointment;
    }
}