package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleMenuPermissionUpdateRequestDTO {

    private Long roleId;

    private List<MenuPermissionUpdateRequestDTO> menuPermissions;

}
