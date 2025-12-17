package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuPermissionUpdateRequestDTO {

    private Long menuId;

    private List<Long> permissionIds;

}
