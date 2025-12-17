package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Contractor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractorRepository extends JpaRepository<Contractor, Long> {

    List<Contractor> findByActiveTrue(Sort sort);


}
