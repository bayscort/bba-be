package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Mill;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MillRepository extends JpaRepository<Mill, Long> {

    List<Mill> findByActiveTrue(Sort sort);


}
