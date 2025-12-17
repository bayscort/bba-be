package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.BlockDTO;
import com.project.bbapalmchain.model.Block;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BlockMapper {

    Block toEntity(BlockDTO dto);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Block entity, BlockDTO dto);

}
