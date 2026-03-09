package com.project.bbapalmchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashflowItemDTO {
    private String financeItem;
    
    // Total item per bulan (Index 0 = Jan, 11 = Des)
    private BigDecimal[] monthlyAmount;
    
    // Total item setahun
    private BigDecimal yearlyTotal;
}