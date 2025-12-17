package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.RoleMenuPermissionRequestDTO;
import com.project.bbapalmchain.dto.RoleMenuPermissionResponseDTO;
import com.project.bbapalmchain.model.RoleMenuPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMenuPermissionMapper {

    RoleMenuPermission toEntity(RoleMenuPermissionRequestDTO dto);

    RoleMenuPermissionResponseDTO toDTO(RoleMenuPermission entity);

    @Mapping(target = "id", ignore = true)
    RoleMenuPermission toUpdate(@MappingTarget RoleMenuPermission entity, RoleMenuPermissionRequestDTO dto);

}
