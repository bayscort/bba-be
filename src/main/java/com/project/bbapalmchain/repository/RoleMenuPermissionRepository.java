package com.project.bbapalmchain.repository;

import com.project.bbapalmchain.model.Role;
import com.project.bbapalmchain.model.RoleMenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleMenuPermissionRepository extends JpaRepository<RoleMenuPermission, Long> {
    List<RoleMenuPermission> findByRole(Role role);

    @Modifying
    @Query("DELETE FROM RoleMenuPermission rmp WHERE rmp.role.id = :roleId")
    void deleteByRoleId(Long roleId);

}
