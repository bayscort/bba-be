package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.FinanceItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinanceItemRepository extends JpaRepository<FinanceItem, Long> {

    List<FinanceItem> findByActiveTrue(Sort sort);


}
