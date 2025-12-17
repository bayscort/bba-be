package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.model.Account;
import com.project.bbapalmchain.model.FinanceItem;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ReceiptReqDTO {

    private LocalDate receiptDate;

    private BigDecimal amount;

    private Long financeItemId;

    private boolean isReconciled;

    private Long accountId;

    private String note;

}
