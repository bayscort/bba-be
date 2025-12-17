package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Driver;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByActiveTrue(Sort sort);


}
