package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.RateVersioning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RateVersioningRepository extends JpaRepository<RateVersioning, Long> {

    @Query("""
        SELECT rv FROM RateVersioning rv
        JOIN RateConfiguration rc ON rc.rateVersioning.id = rv.id
        WHERE rc.afdeling.id = :afdelingId
          AND rc.mill.id = :millId
          AND rv.isActive = true
    """)
    List<RateVersioning> findActiveByAfdelingAndMill(Long afdelingId, Long millId);

}
