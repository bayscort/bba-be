package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}