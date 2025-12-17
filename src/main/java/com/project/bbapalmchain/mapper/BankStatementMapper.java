package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.BankStatementDTO;
import com.project.bbapalmchain.model.BankStatement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankStatementMapper {

    BankStatementDTO toDTO(BankStatement entity);


}
