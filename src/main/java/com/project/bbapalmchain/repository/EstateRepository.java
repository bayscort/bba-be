package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Estate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstateRepository extends JpaRepository<Estate, Long> {

    List<Estate> findByActiveTrue(Sort sort);


}
