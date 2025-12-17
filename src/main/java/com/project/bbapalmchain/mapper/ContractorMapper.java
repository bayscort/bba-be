package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.ContractorDTO;
import com.project.bbapalmchain.dto.ContractorRequestDTO;
import com.project.bbapalmchain.model.Contractor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContractorMapper {

    Contractor toEntity(ContractorRequestDTO dto);
    Contractor toEntity(ContractorDTO dto);

    ContractorDTO toDTO(Contractor entity);

    @Mapping(target = "id", ignore = true)
    void toUpdate(@MappingTarget Contractor entity, ContractorRequestDTO dto);

}
