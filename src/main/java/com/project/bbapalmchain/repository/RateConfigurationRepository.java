package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.RateConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RateConfigurationRepository extends JpaRepository<RateConfiguration, Long> {

    @Query("""
        SELECT rc FROM RateConfiguration rc
        JOIN rc.rateVersioning rv
        WHERE rc.afdeling.id = :afdelingId
          AND rc.mill.id = :millId
          AND rv.effectiveDate <= :date
          AND rv.isActive = true
        ORDER BY rv.effectiveDate DESC
        LIMIT 1
    """)
    Optional<RateConfiguration> findEffectiveRate(Long afdelingId, Long millId, LocalDate date);

    List<RateConfiguration> findAllByAfdelingIdAndMillIdOrderByRateVersioningEffectiveDateDesc(Long afdId, Long millId);

    @Query("""
    SELECT rc FROM RateConfiguration rc
    JOIN FETCH rc.afdeling a
    JOIN FETCH rc.mill m
    JOIN FETCH rc.rateVersioning v
    WHERE v.isActive = true
""")
    List<RateConfiguration> findAllActiveRates();

}
