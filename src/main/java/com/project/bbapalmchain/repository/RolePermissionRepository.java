package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Role;
import com.project.bbapalmchain.model.RoleMenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RoleMenuPermission, Long> {

    List<RoleMenuPermission> findByRole(Role role);

}
