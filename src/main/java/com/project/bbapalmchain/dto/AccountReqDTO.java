package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountReqDTO {

    private Long id;

    private String name;

    private AccountType accountType;

    private String accountNumber;

    private BigDecimal balance;

    private Boolean active;

    private Long estateId;

}
