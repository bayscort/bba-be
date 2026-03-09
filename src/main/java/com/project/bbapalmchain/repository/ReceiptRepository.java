package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.dto.SummaryFinancePerItemDTO;
import com.project.bbapalmchain.dto.projection.CashflowProjection;
import com.project.bbapalmchain.dto.projection.ProfitLossPerDayProjection;
import com.project.bbapalmchain.dto.projection.SummaryFinancePerItemProjection;
import com.project.bbapalmchain.model.Receipt;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByAccountIdAndReceiptDateBetween(Long accountId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Receipt r WHERE r.account.id = :accountId AND r.receiptDate < :date")
    BigDecimal sumAmountByAccountIdAndDateBefore(@Param("accountId") Long accountId, @Param("date") LocalDate date);

    List<Receipt> findReceiptByReceiptDateBetween(Sort sort, LocalDate startDate, LocalDate endDate);
    List<Receipt> findReceiptByAccountIdAndReceiptDateBetween(Sort sort, Long accountId, LocalDate startDate, LocalDate endDate);

    List<Receipt> findByAccountIdAndReceiptDateBetweenAndIdNotIn(
            Long accountId, LocalDate startDate, LocalDate endDate, Collection<Long> ids);

    @Query(value = "SELECT COALESCE(SUM(r.amount), 0) FROM {h-schema}receipt r " +
            "WHERE r.account_id = :accountId AND r.receipt_date BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    BigDecimal sumTotalReceiptsByDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT receipt_date AS date, SUM(amount) AS totalProfitLoss " +
            "FROM {h-schema}receipt " +
            "WHERE account_id = :accountId AND receipt_date BETWEEN :startDate AND :endDate " +
            "GROUP BY receipt_date",
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
            "FROM Receipt e JOIN e.financeItem fi " +
            "WHERE e.account.id = :accountId " +
            "  AND e.receiptDate >= COALESCE(:startDate, e.receiptDate) " +
            "  AND e.receiptDate <= COALESCE(:endDate, e.receiptDate) " +
            "GROUP BY fi.id, fi.name, fi.itemCategory " +
            "ORDER BY fi.itemCategory ASC, totalAmount DESC")
    List<SummaryFinancePerItemProjection> findSummaryByFinanceItem(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = "SELECT " +
            "    fi.item_category AS itemCategory, " +
            "    fi.name AS financeItem, " +
            "    CAST(EXTRACT(MONTH FROM r.receipt_date) AS INTEGER) AS month, " +
            "    SUM(r.amount) AS totalAmount " +
            "FROM {h-schema}receipt r " +
            "JOIN {h-schema}finance_item fi ON r.finance_item_id = fi.id " +
            "WHERE r.account_id = :accountId " +
            "  AND CAST(EXTRACT(YEAR FROM r.receipt_date) AS INTEGER) = :year " +
            "GROUP BY fi.item_category, fi.name, CAST(EXTRACT(MONTH FROM r.receipt_date) AS INTEGER) " +
            "ORDER BY fi.item_category, fi.name, month",
            nativeQuery = true)
    List<CashflowProjection> findCashflowByYear(
            @Param("accountId") Long accountId,
            @Param("year") Integer year);

    @Query(value = "SELECT " +
            "    fi.item_category AS itemCategory, " +
            "    fi.name AS financeItem, " +
            "    CAST(EXTRACT(MONTH FROM r.receipt_date) AS INTEGER) AS month, " +
            "    SUM(r.amount) AS totalAmount " +
            "FROM {h-schema}receipt r " +
            "JOIN {h-schema}finance_item fi ON r.finance_item_id = fi.id " +
            "WHERE r.account_id IN (:accountIds) " + // Perubahan di sini (IN)
            "  AND CAST(EXTRACT(YEAR FROM r.receipt_date) AS INTEGER) = :year " +
            "GROUP BY fi.item_category, fi.name, CAST(EXTRACT(MONTH FROM r.receipt_date) AS INTEGER) " +
            "ORDER BY fi.item_category, fi.name, month",
            nativeQuery = true)
    List<CashflowProjection> findCashflowByAccountIdsAndYear(
            @Param("accountIds") Collection<Long> accountIds,
            @Param("year") Integer year);



}
