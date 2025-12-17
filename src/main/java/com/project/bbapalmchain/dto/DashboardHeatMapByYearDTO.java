package com.project.bbapalmchain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardHeatMapByYearDTO {

    private Integer month;
    private Integer day;
    private Double avgLoadWeight;

}
