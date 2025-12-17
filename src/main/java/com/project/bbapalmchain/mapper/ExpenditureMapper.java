package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.ExpenditureDTO;
import com.project.bbapalmchain.dto.ExpenditureReqDTO;
import com.project.bbapalmchain.model.Expenditure;
import com.project.bbapalmchain.model.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExpenditureMapper {

    Expenditure toEntity(ExpenditureReqDTO dto);

    ExpenditureDTO toDTO(Expenditure entity);

}
