package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.ReceiptDTO;
import com.project.bbapalmchain.dto.ReceiptReqDTO;
import com.project.bbapalmchain.model.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    Receipt toEntity(ReceiptReqDTO dto);

    ReceiptDTO toDTO(Receipt entity);

}
