package com.project.bbapalmchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MenuDTO {

    private String name;
    private List<String> permissions;

}
