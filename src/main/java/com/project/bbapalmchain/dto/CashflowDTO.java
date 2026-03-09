package com.project.bbapalmchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashflowDTO {
    private String itemCategory;
    private List<CashflowItemDTO> items;
    
    // Total akumulasi kategori per bulan (Index 0 = Jan, 11 = Des)
    private BigDecimal[] monthlyTotal;
    
    // Total akumulasi kategori setahun
    private BigDecimal yearlyTotal;
}