package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.AfdelingDTO;
import com.project.bbapalmchain.dto.AfdelingResponseDTO;
import com.project.bbapalmchain.model.Afdeling;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AfdelingMapper {

    Afdeling toEntity(AfdelingDTO dto);

    AfdelingDTO toDTO(Afdeling entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Afdeling entity, AfdelingDTO dto);

}
