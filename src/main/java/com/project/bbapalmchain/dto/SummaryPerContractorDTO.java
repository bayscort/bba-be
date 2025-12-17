package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SummaryPerContractorDTO {

    private ContractorDTO contractor;
    private Integer totalTrips;
    private BigDecimal totalLoad;
    private BigDecimal totalRevenue;
    private BigDecimal totalContractorExpenses;
    private BigDecimal totalFeeOperational;
    private BigDecimal totalExpenses;
    private BigDecimal profitLoss;
    private List<VehicleDTO> vehicles;

}
