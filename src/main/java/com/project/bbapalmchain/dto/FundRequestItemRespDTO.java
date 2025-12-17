package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FundRequestItemRespDTO {

    private Long id;

    private FinanceItemDTO financeItem;

    private String description;

    private BigDecimal amount;

    private String bankAccountNumber;

}
