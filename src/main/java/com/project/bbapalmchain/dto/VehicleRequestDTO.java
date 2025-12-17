package com.project.bbapalmchain.dto;

import com.project.bbapalmchain.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequestDTO {

    private Long id;

    private String licensePlatNumber;

    private VehicleType vehicleType;

}
