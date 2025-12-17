package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManualReconciliationRequestDTO {

    private Long bankStatementId;
    private Long receiptId;
    private Long expenditureId;

}
