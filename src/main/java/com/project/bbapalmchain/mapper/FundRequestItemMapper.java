package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.FundRequestDTO;
import com.project.bbapalmchain.dto.FundRequestItemDTO;
import com.project.bbapalmchain.model.FundRequest;
import com.project.bbapalmchain.model.FundRequestItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FundRequestItemMapper {

    FundRequestItem toEntity(FundRequestItemDTO dto);

    FundRequestItemDTO toDTO(FundRequestItem entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget FundRequestItem entity, FundRequestItemDTO dto);

}
