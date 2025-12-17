package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.MenuRequestDTO;
import com.project.bbapalmchain.dto.MenuResponseDTO;
import com.project.bbapalmchain.model.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    Menu toEntity(MenuRequestDTO dto);

    MenuResponseDTO toDTO(Menu entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Menu entity, MenuRequestDTO dto);

}
