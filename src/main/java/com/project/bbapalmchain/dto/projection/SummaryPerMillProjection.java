package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;

public interface SummaryPerMillProjection {

    Long getMillId();
    String getMillName();
    Integer getTotalTrips();
    BigDecimal getTotalLoad();
    BigDecimal getTotalRevenue();
    BigDecimal getTotalContractorExpenses();
    BigDecimal getTotalFeeOperational();
    BigDecimal getTotalExpenses();
    BigDecimal getProfitLoss();

}
