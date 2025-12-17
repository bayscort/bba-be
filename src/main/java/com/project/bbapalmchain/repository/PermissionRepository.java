package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}