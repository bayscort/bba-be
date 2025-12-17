package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Afdeling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AfdelingRepository extends JpaRepository<Afdeling, Long> {

    List<Afdeling> findByEstateId(Long estateId);

}
