package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.PermissionRequestDTO;
import com.project.bbapalmchain.dto.PermissionResponseDTO;
import com.project.bbapalmchain.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toEntity(PermissionRequestDTO dto);

    PermissionResponseDTO toDTO(Permission entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Permission entity, PermissionRequestDTO dto);

}
