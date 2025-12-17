package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.model.Account;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BankStatementDTO {

    private Long id;

    private String currency;

    private LocalDateTime postDate;

    private String remarks;

    private String additionalDesc;

    private BigDecimal debitAmount;

    private BigDecimal creditAmount;

    private BigDecimal closingBalance;

}
