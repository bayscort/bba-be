package com.project.bbapalmchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SummaryFinancePerItemDTO {

    private String financeItem;
    private Integer totalTransaction;
    private BigDecimal totalAmount;

}
