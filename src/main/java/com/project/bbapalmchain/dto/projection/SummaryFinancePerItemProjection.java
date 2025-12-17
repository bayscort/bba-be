package com.project.bbapalmchain.dto.projection;

import java.math.BigDecimal;

public interface SummaryFinancePerItemProjection {

    String getFinanceItem();
    String getItemCategory();
    Integer getTotalTransaction();
    BigDecimal getTotalAmount();

}
