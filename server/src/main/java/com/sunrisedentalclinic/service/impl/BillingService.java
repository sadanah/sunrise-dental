package com.sunrisedentalclinic.service.impl;

import com.sunrisedentalclinic.dao.AppointmentDAO;
import com.sunrisedentalclinic.dao.BillDAO;
import com.sunrisedentalclinic.dao.DentistDAO;
import com.sunrisedentalclinic.dao.TreatmentTypeDAO;
import com.sunrisedentalclinic.domain.*;
import com.sunrisedentalclinic.exception.AppointmentNotFoundException;
import com.sunrisedentalclinic.service.IBillingService;
import com.sunrisedentalclinic.service.strategy.DiscountBillingStrategy;
import com.sunrisedentalclinic.service.strategy.IBillingStrategy;
import com.sunrisedentalclinic.service.strategy.StandardBillingStrategy;

import java.math.BigDecimal;
import java.util.UUID;

public class BillingService implements IBillingService {

    private final AppointmentDAO appointmentDAO;
    private final BillDAO billDAO;
    private final DentistDAO dentistDAO;
    private final TreatmentTypeDAO treatmentTypeDAO;

    public BillingService(AppointmentDAO appointmentDAO, BillDAO billDAO,
                          DentistDAO dentistDAO, TreatmentTypeDAO treatmentTypeDAO) {
        this.appointmentDAO = appointmentDAO;
        this.billDAO = billDAO;
        this.dentistDAO = dentistDAO;
        this.treatmentTypeDAO = treatmentTypeDAO;
    }

    @Override
    public Bill calculateBill(String appointmentNo) {
        Appointment appointment = appointmentDAO.findById(appointmentNo);
        if (appointment == null) {
            throw new AppointmentNotFoundException("No appointment found: " + appointmentNo);
        }

        Dentist dentist = dentistDAO.findById(appointment.getDentistID());
        TreatmentType treatment = treatmentTypeDAO.findById(appointment.getTreatmentID());

        BigDecimal consultationFee = dentist.getConsultationFee();
        BigDecimal treatmentCost = treatment.getBaseCost();

        IBillingStrategy strategy = new StandardBillingStrategy();
        BigDecimal total = strategy.calculate(appointment, consultationFee, treatmentCost);

        String billID = "B" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Bill bill = new Bill(billID, consultationFee, treatmentCost);
        bill.setAppointmentNo(appointmentNo);

        billDAO.save(bill);
        return bill;
    }

    @Override
    public Bill applyDiscount(Bill bill, BigDecimal discountPercent) {
        Appointment appointment = appointmentDAO.findById(bill.getAppointmentNo());

        IBillingStrategy strategy = new DiscountBillingStrategy(discountPercent);
        BigDecimal discountedTotal = strategy.calculate(appointment, bill.getConsultationFee(), bill.getTreatmentCost());

        Bill discountedBill = new Bill(bill.getBillID(), bill.getConsultationFee(), bill.getTreatmentCost());
        discountedBill.setAppointmentNo(bill.getAppointmentNo());
        discountedBill.overrideTotalAmount(discountedTotal); // see note below

        billDAO.update(discountedBill);
        return discountedBill;
    }

    @Override
    public void printReceipt(String billID) {
        Bill bill = getBillById(billID);
        System.out.println(bill.printReceipt());
    }

    @Override
    public Bill getBillById(String billID) {
        return billDAO.findById(billID);
    }
}