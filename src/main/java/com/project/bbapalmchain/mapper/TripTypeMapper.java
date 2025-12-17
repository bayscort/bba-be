package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.TripTypeDTO;
import com.project.bbapalmchain.dto.TripTypeRequestDTO;
import com.project.bbapalmchain.model.TripType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TripTypeMapper {

    TripType toEntity(TripTypeDTO dto);
    TripType toEntity(TripTypeRequestDTO dto);

    TripTypeDTO toDTO(TripType entity);

    @Mapping(target = "id", ignore = true)
    TripType toUpdate(@MappingTarget TripType entity, TripTypeRequestDTO dto);

}
