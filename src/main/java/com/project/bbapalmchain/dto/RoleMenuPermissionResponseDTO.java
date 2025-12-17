package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.model.Permission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMenuPermissionResponseDTO {

    private Long id;

    private RoleResponseDTO role;

    private MenuResponseDTO menu;

    private PermissionResponseDTO permission;

}
