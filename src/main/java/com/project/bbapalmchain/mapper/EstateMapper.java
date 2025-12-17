package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.EstateRequestDTO;
import com.project.bbapalmchain.dto.EstateResponseDTO;
import com.project.bbapalmchain.model.Estate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EstateMapper {

    Estate toEntity(EstateRequestDTO dto);

    EstateResponseDTO toDTO(Estate entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Estate entity, EstateRequestDTO dto);

}
