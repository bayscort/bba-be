package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenditureReqDTO {

    private LocalDate expenditureDate;

    private BigDecimal amount;

    private Long financeItemId;

    private boolean isReconciled;

    private Long accountId;

    private String note;

}
