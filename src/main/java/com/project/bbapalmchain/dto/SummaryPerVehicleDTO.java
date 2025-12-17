package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SummaryPerVehicleDTO {

    private VehicleDTO vehicle;
    private Integer totalTrips;
    private BigDecimal totalLoad;
    private BigDecimal totalRevenue;
    private BigDecimal totalContractorExpenses;
    private BigDecimal totalFeeOperational;
    private BigDecimal totalExpenses;
    private BigDecimal profitLoss;

}
