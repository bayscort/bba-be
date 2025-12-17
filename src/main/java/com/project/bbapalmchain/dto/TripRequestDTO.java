package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TripRequestDTO {

    private LocalDate date;

    private Long tripTypeId;

    private Long millId;

    private Long afdelingId;

    private Long driverId;

    private Long vehicleId;

    private Long contractorId;

    private BigDecimal loadWeightKg;

    private BigDecimal ptpnRate;

    private BigDecimal contractorRate;

    private BigDecimal travelAllowance;

    private BigDecimal loadingFee;

    private BigDecimal consumptionFee;
    private BigDecimal additionalFee1;

    private BigDecimal additionalFee2;

    private BigDecimal additionalFee3;


}
