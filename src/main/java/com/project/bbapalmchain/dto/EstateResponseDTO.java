package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EstateResponseDTO {

    private Long id;

    private String name;

    private List<AfdelingDTO> afdelingList;

}
