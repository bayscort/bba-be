package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuDetailDTO {

    private Long id;
    private String name;
    private List<PermissionResponseDTO> permissionList;

}
