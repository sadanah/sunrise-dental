package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.BillDAO;
import com.sunrisedentalclinic.domain.Bill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RevenueReport extends Report {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BillDAO billDAO;
    private BigDecimal totalRevenue;

    public RevenueReport(String reportID, String generatedBy, LocalDate startDate, LocalDate endDate, BillDAO billDAO) {
        super(reportID, generatedBy);
        this.startDate = startDate;
        this.endDate = endDate;
        this.billDAO = billDAO;
    }

    @Override
    public void generate() {
        List<Bill> bills = billDAO.findByDateRange(startDate, endDate);
        totalRevenue = calculateTotalRevenue(bills);
    }

    public BigDecimal calculateTotalRevenue(List<Bill> bills) {
        return bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
}