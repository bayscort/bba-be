package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DashboardSummaryProjection {

    BigDecimal getTotalRevenue();
    BigDecimal getTotalExpenses();
    BigDecimal getTotalProfitLosses();

    BigDecimal getPreviousTotalRevenue();
    BigDecimal getPreviousTotalExpenses();
    BigDecimal getPreviousTotalProfitLosses();

    Double getRevenueChangePercentage();
    Double getExpenseChangePercentage();
    Double getProfitLossChangePercentage();

}
