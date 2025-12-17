package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.BankStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BankStatementRepository extends JpaRepository<BankStatement, Long> {

    List<BankStatement> findByAccountIdAndPostDateBetweenOrderByPostDateAsc(Long accountId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<BankStatement> findByAccountIdAndPostDateBetweenAndIdNotIn(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate, Collection<Long> ids);

}
