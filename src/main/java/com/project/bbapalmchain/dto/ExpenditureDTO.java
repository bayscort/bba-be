package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExpenditureDTO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDate expenditureDate;

    private BigDecimal amount;

    private FinanceItemDTO financeItem;

    private boolean isReconciled;

    private AccountDTO account;

    private String note;

}
