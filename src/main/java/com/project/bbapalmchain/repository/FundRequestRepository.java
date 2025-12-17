package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.FundRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface FundRequestRepository extends JpaRepository<FundRequest, Long> {

    boolean existsByFundRequestCode(String fundRequestCode);

    List<FundRequest> findFundRequestByNextApprovalRoleId(Long nextApprovalRoleId);

    List<FundRequest> findFundRequestByDateBetween(Sort sort, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(fr) FROM FundRequest fr WHERE MONTH(fr.date) = :month AND YEAR(fr.date) = :year")
    Long countByMonthAndYear(@Param("month") int month, @Param("year") int year);


}
