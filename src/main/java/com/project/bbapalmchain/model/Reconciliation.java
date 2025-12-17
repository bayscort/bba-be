package com.project.bbapalmchain.model;

import com.project.bbapalmchain.dto.base.AuditableEntity;
import com.project.bbapalmchain.enums.ReconciliationType;
import com.project.bbapalmchain.enums.VehicleType;
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
public class Reconciliation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_statement_id", nullable = false)
    private BankStatement bankStatement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", unique = true)
    private Receipt receipt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expenditure_id", unique = true)
    private Expenditure expenditure;

    @Column(name = "reconciliation_date", nullable = false)
    private LocalDateTime reconciliationDate;

    @Enumerated(EnumType.STRING)
    private ReconciliationType reconciliationType;

}
