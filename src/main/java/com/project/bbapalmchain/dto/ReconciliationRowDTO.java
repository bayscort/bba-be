package com.project.bbapalmchain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.bbapalmchain.model.BankStatement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReconciliationRowDTO {

    private Long id;

    private LocalDateTime date;

    private String status;

    private BankStatementDTO bankStatement;

    private InternalTransactionDTO internalTransaction;

    private Long reconciliationId;
}
