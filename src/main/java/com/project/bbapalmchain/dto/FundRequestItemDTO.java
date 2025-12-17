package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FundRequestItemDTO {

    private Long id;

    private Long financeItemId;

    private String description;

    private BigDecimal amount;

    private String bankAccountNumber;

}
