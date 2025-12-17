package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.VehicleDTO;
import com.project.bbapalmchain.dto.VehicleRequestDTO;
import com.project.bbapalmchain.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toEntity(VehicleDTO dto);
    Vehicle toEntity(VehicleRequestDTO dto);

    VehicleDTO toDTO(Vehicle entity);

    @Mapping(target = "id", ignore = true)
    Vehicle toUpdate(@MappingTarget Vehicle entity, VehicleRequestDTO dto);

}
