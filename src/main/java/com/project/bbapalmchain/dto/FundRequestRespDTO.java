package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class FundRequestRespDTO {

    private Long id;

    private String fundRequestCode;

    private Long nextApprovalRoleId;

    private LocalDate date;

    private BigDecimal totalAmount;

    private String totalAmountInWords;

    private List<FundRequestApprovalLogDTO> fundRequestApprovalLogList;

    private List<FundRequestItemRespDTO> fundRequestItemList;

}
