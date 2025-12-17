package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.model.Menu;
import com.project.bbapalmchain.model.Permission;
import com.project.bbapalmchain.model.Role;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMenuPermissionRequestDTO {

    private Long roleId;

    private Long menuId;

    private Long permissionId;

}
