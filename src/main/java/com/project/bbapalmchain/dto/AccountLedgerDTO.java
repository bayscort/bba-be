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
public class AccountLedgerDTO {

    private Long accountId;
    private String accountName;
    private BigDecimal openingBalance;
    private BigDecimal finalBalance;
    private List<TransactionLedgerItemDTO> transactions;

}
