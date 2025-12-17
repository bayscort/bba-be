package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;

public interface SummaryPerContractorProjection {

    Long getContractorId();
    String getContractorName();
    String getContractorPhoneNumber();
    Integer getTotalTrips();
    BigDecimal getTotalLoad();
    BigDecimal getTotalRevenue();
    BigDecimal getTotalContractorExpenses();
    BigDecimal getTotalFeeOperational();
    BigDecimal getTotalExpenses();
    BigDecimal getProfitLoss();
    String getVehicleIds();

}
