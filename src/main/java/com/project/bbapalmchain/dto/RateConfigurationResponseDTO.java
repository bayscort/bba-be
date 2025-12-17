package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.model.RateVersioning;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RateConfigurationResponseDTO {

    private Long id;

    private AfdelingRateDTO afdeling;

    private MillResponseDTO mill;

    private BigDecimal ptpnRate;

    private BigDecimal contractorRate;

    private String label;

    private RateVersioning rateVersioning;

}
