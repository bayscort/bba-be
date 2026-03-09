package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;

public interface CashflowProjection {
    String getItemCategory();
    String getFinanceItem();
    Integer getMonth(); // 1 - 12
    BigDecimal getTotalAmount();
}