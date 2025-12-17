package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String name;
    private String username;
    private String password;
    private Long roleId;
    private Long estateId;

}
