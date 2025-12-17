package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.enums.ApprovalStage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FundRequestApprovalLogReqDTO {

    private ApprovalStage approvalStage;

    private LocalDateTime stageTimestamp;

    private String notes;

    private Long fundRequestId;

}
