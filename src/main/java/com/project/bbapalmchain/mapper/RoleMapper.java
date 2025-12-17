package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.RoleRequestDTO;
import com.project.bbapalmchain.dto.RoleResponseDTO;
import com.project.bbapalmchain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toEntity(RoleRequestDTO dto);

    RoleResponseDTO toDTO(Role entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Role entity, RoleRequestDTO dto);

}
