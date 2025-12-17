package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TripResponseDTO {

    private Long id;

    private LocalDate date;

    private TripTypeDTO tripType;

    private MillResponseDTO mill;

    private AfdelingDTO afdeling;

    private DriverDTO driver;

    private VehicleDTO vehicle;

    private ContractorDTO contractor;

    private BigDecimal loadWeightKg;

    private BigDecimal ptpnRate;

    private BigDecimal contractorRate;

    private BigDecimal travelAllowance;

    private BigDecimal loadingFee;

    private BigDecimal consumptionFee;
    private BigDecimal additionalFee1;

    private BigDecimal additionalFee2;

    private BigDecimal additionalFee3;


    private LocalDateTime createdAt;

    private String createdBy;

}
