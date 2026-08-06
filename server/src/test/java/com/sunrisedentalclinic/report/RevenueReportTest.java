package com.sunrisedentalclinic.report;

import com.sunrisedentalclinic.dao.BillDAO;
import com.sunrisedentalclinic.domain.Bill;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevenueReportTest {

    @Test
    void generate_sumsAllBillTotals() {
        BillDAO mockDao = mock(BillDAO.class);
        Bill b1 = new Bill("B001", BigDecimal.valueOf(5000), BigDecimal.valueOf(3000)); // total 8000
        Bill b2 = new Bill("B002", BigDecimal.valueOf(5000), BigDecimal.valueOf(5000)); // total 10000
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        when(mockDao.findByDateRange(start, end)).thenReturn(List.of(b1, b2));

        RevenueReport report = new RevenueReport("RPT001", "A001", start, end, mockDao);
        report.generate();

        assertEquals(0, BigDecimal.valueOf(18000).compareTo(report.getTotalRevenue()));
        verify(mockDao).findByDateRange(start, end);
    }

    @Test
    void generate_noMatchingBills_returnsZero() {
        BillDAO mockDao = mock(BillDAO.class);
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        when(mockDao.findByDateRange(start, end)).thenReturn(List.of());

        RevenueReport report = new RevenueReport("RPT002", "A001", start, end, mockDao);
        report.generate();

        assertEquals(0, BigDecimal.ZERO.compareTo(report.getTotalRevenue()));
    }

    @Test
    void calculateTotalRevenue_directCall_sumsCorrectly() {
        Bill b1 = new Bill("B003", BigDecimal.valueOf(2000), BigDecimal.valueOf(1000)); // 3000
        RevenueReport report = new RevenueReport("RPT003", "A001",
                LocalDate.now(), LocalDate.now(), mock(BillDAO.class));

        BigDecimal result = report.calculateTotalRevenue(List.of(b1));

        assertEquals(0, BigDecimal.valueOf(3000).compareTo(result));
    }
}