package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.MillRequestDTO;
import com.project.bbapalmchain.dto.MillResponseDTO;
import com.project.bbapalmchain.model.Mill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MillMapper {

    Mill toEntity(MillRequestDTO dto);

    MillResponseDTO toDTO(Mill entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Mill entity, MillRequestDTO dto);

}
