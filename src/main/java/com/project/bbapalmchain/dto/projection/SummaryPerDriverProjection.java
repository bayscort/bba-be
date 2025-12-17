package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;

public interface SummaryPerDriverProjection {

    Long getDriverId();
    String getDriverName();
    String getDriverLicenseNumber();
    Integer getTotalTrips();
    BigDecimal getTotalLoad();
    BigDecimal getTotalRevenue();
    BigDecimal getTotalContractorExpenses();
    BigDecimal getTotalFeeOperational();
    BigDecimal getTotalExpenses();
    BigDecimal getProfitLoss();

}
