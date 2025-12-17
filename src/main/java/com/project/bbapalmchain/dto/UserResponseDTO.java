package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;

    private String name;
    private String username;
    private RoleResponseDTO role;
    private EstateResponseDTO estate;

}
