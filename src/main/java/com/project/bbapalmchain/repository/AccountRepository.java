package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Account;
import com.project.bbapalmchain.model.Estate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByActiveTrue(Sort sort);

    @Query("SELECT a FROM Account a WHERE a.active = true " +
            "AND (:estate IS NULL OR a.estate = :estate)")
    List<Account> findActiveByEstateOrAll(@Param("estate") Estate estate, Sort sort);


    Optional<Account> findByAccountNumber(String accountNumber);


}
