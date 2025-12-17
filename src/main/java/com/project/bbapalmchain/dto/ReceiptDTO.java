package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReceiptDTO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDate receiptDate;

    private BigDecimal amount;

    private FinanceItemDTO financeItem;

    private boolean isReconciled;

    private AccountDTO account;

    private String note;

}
