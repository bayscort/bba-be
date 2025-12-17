package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AfdelingRateDTO {

    private Long id;

    private String name;
    private String estateName;

    private List<BlockDTO> blockList;

}
