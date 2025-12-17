package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.UserRequestDTO;
import com.project.bbapalmchain.dto.UserResponseDTO;
import com.project.bbapalmchain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toDTO(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUpdate(@MappingTarget User entity, UserRequestDTO dto);
}
