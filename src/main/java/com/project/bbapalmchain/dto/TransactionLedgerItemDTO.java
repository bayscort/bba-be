package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionLedgerItemDTO {

    private LocalDateTime createdAt;
    private LocalDate date;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal runningBalance;
    private TransactionType type;
    private String note;


}
