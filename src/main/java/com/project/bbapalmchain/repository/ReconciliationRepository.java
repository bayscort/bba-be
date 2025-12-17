package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {

    List<Reconciliation> findByBankStatement_AccountIdAndBankStatement_PostDateBetween(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate);

}
