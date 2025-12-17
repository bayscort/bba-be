package com.project.bbapalmchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SummaryFinancePerCategoryDTO {

    private String itemCategory;
    private List<SummaryFinancePerItemDTO> items;
    private Integer totalTransaction;
    private BigDecimal totalAmount;

}
