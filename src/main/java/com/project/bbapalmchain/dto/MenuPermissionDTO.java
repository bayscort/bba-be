package com.project.bbapalmchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MenuPermissionDTO {
    private String name;
    private List<String> permissions;
}
