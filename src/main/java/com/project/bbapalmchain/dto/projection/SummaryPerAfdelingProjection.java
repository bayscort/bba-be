package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;

public interface SummaryPerAfdelingProjection {

    Long getAfdelingId();
    String getAfdelingName();
    Integer getTotalTrips();
    BigDecimal getTotalLoad();
    BigDecimal getTotalRevenue();
    BigDecimal getTotalContractorExpenses();
    BigDecimal getTotalFeeOperational();
    BigDecimal getTotalExpenses();
    BigDecimal getProfitLoss();

}
