package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.AccountDTO;
import com.project.bbapalmchain.dto.AccountReqDTO;
import com.project.bbapalmchain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(AccountReqDTO dto);

    AccountDTO toDTO(Account entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void toUpdate(@MappingTarget Account entity, AccountReqDTO dto);

}
