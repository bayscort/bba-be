package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RateConfigurationRequestDTO {

    private Long afdelingId;

    private Long millId;

    private BigDecimal ptpnRate;

    private BigDecimal contractorRate;

    private String label;

}
