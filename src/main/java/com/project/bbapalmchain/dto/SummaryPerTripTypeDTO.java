package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SummaryPerTripTypeDTO {
    private TripTypeDTO tripType;
    private Long totalTrips;
    private Double totalLoad;
    private Double totalRevenue;
    private Double totalContractorExpenses;
    private Double totalFeeOperational;
    private Double totalExpenses;
    private Double profitLoss;
}
