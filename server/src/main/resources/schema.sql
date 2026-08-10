CREATE TABLE patient (
                         personID INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(100) NOT NULL,
                         contactNo VARCHAR(20),
                         address VARCHAR(255),
                         patientID VARCHAR(20) UNIQUE NOT NULL,
                         registeredDate DATE,
                         email VARCHAR(255) NULL
);

CREATE TABLE staff (
                       personID INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(100) NOT NULL,
                       contactNo VARCHAR(20),
                       address VARCHAR(255),
                       staffID VARCHAR(20) UNIQUE NOT NULL,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       passwordHash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL, -- RECEPTIONIST/DENTIST/ADMIN
                       specialization VARCHAR(100) NULL, -- only used for DENTIST
                       consultationFee DECIMAL(10,2) NULL, -- only used for DENTIST
                       email VARCHAR(255) NULL
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
                             staffID VARCHAR(20) NOT NULL, -- who registered the appointmet
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

-- trigger to prevent double booking
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