-- ============================================
-- Sunrise Dental Clinic — TEST database schema
-- ============================================

DROP DATABASE IF EXISTS sunrise_dental_test;
CREATE DATABASE sunrise_dental_test;
USE sunrise_dental_test;

-- ============================================
-- TABLES
-- ============================================

CREATE TABLE patient (
                         personID INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(100) NOT NULL,
                         contactNo VARCHAR(20),
                         address VARCHAR(255),
                         patientID VARCHAR(20) UNIQUE NOT NULL,
                         registeredDate DATE
);

CREATE TABLE staff (
                       personID INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(100) NOT NULL,
                       contactNo VARCHAR(20),
                       address VARCHAR(255),
                       staffID VARCHAR(20) UNIQUE NOT NULL,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       passwordHash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       specialization VARCHAR(100) NULL,
                       consultationFee DECIMAL(10,2) NULL
);

CREATE TABLE treatment_type (
                                treatmentID VARCHAR(20) PRIMARY KEY,
                                treatmentName VARCHAR(100) NOT NULL,
                                baseCost DECIMAL(10,2) NOT NULL
);

CREATE TABLE appointment (
                             appointmentNo VARCHAR(20) PRIMARY KEY,
                             patientID VARCHAR(20) NOT NULL,
                             dentistID VARCHAR(20) NOT NULL,
                             treatmentID VARCHAR(20) NOT NULL,
                             staffID VARCHAR(20) NOT NULL,
                             appointmentDate DATE NOT NULL,
                             appointmentTime TIME NOT NULL,
                             status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
                             FOREIGN KEY (patientID) REFERENCES patient(patientID),
                             FOREIGN KEY (dentistID) REFERENCES staff(staffID),
                             FOREIGN KEY (treatmentID) REFERENCES treatment_type(treatmentID),
                             FOREIGN KEY (staffID) REFERENCES staff(staffID)
);

CREATE TABLE bill (
                      billID VARCHAR(20) PRIMARY KEY,
                      appointmentNo VARCHAR(20) NOT NULL,
                      consultationFee DECIMAL(10,2) NOT NULL,
                      treatmentCost DECIMAL(10,2) NOT NULL,
                      totalAmount DECIMAL(10,2) NOT NULL,
                      generatedDate DATETIME NOT NULL,
                      FOREIGN KEY (appointmentNo) REFERENCES appointment(appointmentNo)
);

-- ============================================
-- TRIGGER — double-booking prevention
-- ============================================

DELIMITER //
CREATE TRIGGER prevent_double_booking
    BEFORE INSERT ON appointment
    FOR EACH ROW
BEGIN
    DECLARE existing_count INT;
    SELECT COUNT(*) INTO existing_count
    FROM appointment
    WHERE dentistID = NEW.dentistID
      AND appointmentDate = NEW.appointmentDate
      AND appointmentTime = NEW.appointmentTime
      AND status = 'SCHEDULED';
    IF existing_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This dentist already has a scheduled appointment at this date and time';
END IF;
END//
DELIMITER ;

-- ============================================
-- SEED DATA
-- ============================================

-- Staff: one of each role
INSERT INTO staff (name, contactNo, address, staffID, username, passwordHash, role, specialization, consultationFee)
VALUES
    ('Dr. Nadeesha Perera', '0771234567', '12 Galle Road, Colombo', 'D001', 'nperera', 'hashedpass1', 'DENTIST', 'General Dentistry', 2500.00),
    ('Kasun Silva', '0779876543', '45 Kandy Road, Colombo', 'R001', 'ksilva', 'hashedpass2', 'RECEPTIONIST', NULL, NULL),
    ('Amali Fernando', '0761122334', '8 Marine Drive, Colombo', 'A001', 'afernando', 'hashedpass3', 'ADMIN', NULL, NULL);

-- Patient
INSERT INTO patient (name, contactNo, address, patientID, registeredDate)
VALUES
    ('Sadanah Rathnayake', '0755556677', '22 Flower Road, Colombo', 'P001', '2026-01-15');

-- Treatment type
INSERT INTO treatment_type (treatmentID, treatmentName, baseCost)
VALUES
    ('T001', 'Dental Cleaning', 3500.00),
    ('T002', 'Tooth Extraction', 7000.00),
    ('T003', 'Root Canal', 15000.00);

-- One existing SCHEDULED appointment
-- (used to test findByDentistAndDateTime returning a NON-null result on a taken slot,
--  and to test findByPatient / findById against a known, pre-existing record)
INSERT INTO appointment (appointmentNo, patientID, dentistID, treatmentID, staffID, appointmentDate, appointmentTime, status)
VALUES
    ('APT001', 'P001', 'D001', 'T001', 'R001', '2026-08-01', '10:00:00', 'SCHEDULED');

-- One bill linked to APT001
INSERT INTO bill (billID, appointmentNo, consultationFee, treatmentCost, totalAmount, generatedDate)
VALUES
    ('B001', 'APT001', 2500.00, 3500.00, 6000.00, '2026-08-01 10:30:00');