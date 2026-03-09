package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.FundRequestDTO;
import com.project.bbapalmchain.dto.FundRequestRespDTO;
import com.project.bbapalmchain.model.FundRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FundRequestMapper {

    @Mapping(target = "fundRequestApprovalLogList", ignore = true)
    @Mapping(target = "fundRequestItemList", ignore = true)
    @Mapping(target = "fundRequestCode", ignore = true)
    FundRequest toEntity(FundRequestDTO dto);

    FundRequestDTO toDTO(FundRequest entity);

    FundRequestRespDTO toRespDTO(FundRequest entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fundRequestApprovalLogList", ignore = true)
    @Mapping(target = "fundRequestItemList", ignore = true)
//    @Mapping(target = "fundRequestCode", ignore = true)
    void toUpdate(@MappingTarget FundRequest entity, FundRequestDTO dto);

}
