package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EstateRequestDTO {

    private String name;

    private List<AfdelingDTO> afdelingList;

}
