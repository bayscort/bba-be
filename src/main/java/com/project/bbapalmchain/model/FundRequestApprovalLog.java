package com.project.bbapalmchain.model;

import com.project.bbapalmchain.dto.base.AuditableEntity;
import com.project.bbapalmchain.enums.ApprovalStage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FundRequestApprovalLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApprovalStage approvalStage;

    private LocalDateTime stageTimestamp;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "fund_request_id")
    private FundRequest fundRequest;

}
