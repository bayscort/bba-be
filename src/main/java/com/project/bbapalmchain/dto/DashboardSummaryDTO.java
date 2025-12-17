package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DashboardSummaryDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal totalProfitLosses;

    private BigDecimal previousTotalRevenue;
    private BigDecimal previousTotalExpenses;
    private BigDecimal previousTotalProfitLosses;

    private Double revenueChangePercentage;
    private Double expenseChangePercentage;
    private Double profitLossChangePercentage;

}
