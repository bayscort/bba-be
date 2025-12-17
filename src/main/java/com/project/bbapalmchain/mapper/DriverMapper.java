package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.DriverDTO;
import com.project.bbapalmchain.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    Driver toEntity(DriverDTO dto);

    DriverDTO toDTO(Driver entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Driver entity, DriverDTO dto);

}
