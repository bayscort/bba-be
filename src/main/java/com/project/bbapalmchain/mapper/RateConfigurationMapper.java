package com.project.bbapalmchain.mapper;


import com.project.bbapalmchain.dto.RateConfigurationResponseDTO;
import com.project.bbapalmchain.model.RateConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RateConfigurationMapper {

    RateConfigurationResponseDTO toDTO(RateConfiguration entity);


}
