package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.enums.ItemCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceItemDTO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean active;

    private ItemCategory itemCategory;

}
