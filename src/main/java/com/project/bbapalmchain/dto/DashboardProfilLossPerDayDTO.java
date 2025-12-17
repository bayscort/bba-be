package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DashboardProfilLossPerDayDTO {

    private LocalDate date;
    private BigDecimal totalProfitLoss;

}
