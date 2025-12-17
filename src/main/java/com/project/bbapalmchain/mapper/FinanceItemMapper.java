package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.FinanceItemDTO;
import com.project.bbapalmchain.model.FinanceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FinanceItemMapper {

    FinanceItem toEntity(FinanceItemDTO dto);

    FinanceItemDTO toDTO(FinanceItem entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget FinanceItem entity, FinanceItemDTO dto);

}
