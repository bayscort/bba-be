package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.TripType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripTypeRepository extends JpaRepository<TripType, Long> {

    List<TripType> findByActiveTrue(Sort sort);


}
