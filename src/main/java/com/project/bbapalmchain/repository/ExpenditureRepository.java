package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.dto.projection.ProfitLossPerDayProjection;
import com.project.bbapalmchain.dto.projection.SummaryFinancePerItemProjection;
import com.project.bbapalmchain.model.Expenditure;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findByAccountIdAndExpenditureDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expenditure e WHERE e.account.id = :accountId AND e.expenditureDate < :date")
    BigDecimal sumAmountByAccountIdAndDateBefore(@Param("accountId") Long accountId, @Param("date") LocalDate date);

    List<Expenditure> findExpenditureByExpenditureDateBetween(Sort sort, LocalDate startDate, LocalDate endDate);
    List<Expenditure> findExpenditureByAccountIdAndExpenditureDateBetween(Sort sort, Long accountId, LocalDate startDate, LocalDate endDate);

    List<Expenditure> findByAccountIdAndExpenditureDateBetweenAndIdNotIn(
            Long accountId, LocalDate startDate, LocalDate endDate, Collection<Long> ids);

    @Query(value = "SELECT COALESCE(SUM(e.amount), 0) FROM {h-schema}expenditure e " +
            "WHERE e.account_id = :accountId AND e.expenditure_date BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    BigDecimal sumTotalExpendituresByDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT expenditure_date AS date, SUM(amount) AS totalProfitLoss " +
            "FROM {h-schema}expenditure " +
            "WHERE account_id = :accountId AND expenditure_date BETWEEN :startDate AND :endDate " +
            "GROUP BY expenditure_date",
            nativeQuery = true)
    List<ProfitLossPerDayProjection> findDailyTotals(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT " +
            "    fi.name AS financeItem, " +
            "    fi.itemCategory AS itemCategory, " +
            "    SUM(e.amount) AS totalAmount, " +
            "    COUNT(e.id) AS totalTransaction " +
            "FROM Expenditure e JOIN e.financeItem fi " +
            "WHERE e.account.id = :accountId " +
            "  AND e.expenditureDate >= COALESCE(:startDate, e.expenditureDate) " +
            "  AND e.expenditureDate <= COALESCE(:endDate, e.expenditureDate) " +
            "GROUP BY fi.id, fi.name, fi.itemCategory " +
            "ORDER BY fi.itemCategory ASC, totalAmount DESC")
    List<SummaryFinancePerItemProjection> findSummaryByFinanceItem(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


}
