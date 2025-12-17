package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class InternalTransactionDTO {

    private Long id;
    private String type;
    private LocalDateTime date;
    private String description;
    private BigDecimal amount;

}
