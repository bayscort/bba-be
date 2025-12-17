package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleDetailResponseDTO {

    private RoleResponseDTO role;
    private List<MenuDetailDTO> menuList;

}
