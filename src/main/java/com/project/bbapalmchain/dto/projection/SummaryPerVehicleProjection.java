package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;

public interface SummaryPerVehicleProjection {

    Long getVehicleId();
    String getLicensePlatNumber();
    String getVehicleType();
    Integer getTotalTrips();

    BigDecimal getTotalLoad();
    BigDecimal getTotalRevenue();
    BigDecimal getTotalContractorExpenses();
    BigDecimal getTotalFeeOperational();
    BigDecimal getTotalExpenses();
    BigDecimal getProfitLoss();

}
