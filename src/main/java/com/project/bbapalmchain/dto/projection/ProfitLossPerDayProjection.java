package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProfitLossPerDayProjection {

    LocalDate getDate();
    BigDecimal getTotalProfitLoss();

}
