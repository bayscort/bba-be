package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Vehicle;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByActiveTrue(Sort sort);


}
