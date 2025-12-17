package com.project.bbapalmchain.mapper;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.dto.projection.*;
import com.project.bbapalmchain.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TripMapper {

    Trip toEntity(TripRequestDTO dto);

    TripResponseDTO toDTO(Trip entity);

    @Mapping(target = "id", ignore = true)
    Trip toUpdate(@MappingTarget Trip entity, TripRequestDTO dto);

    DashboardSummaryDTO toSummaryDTO(DashboardSummaryProjection projection);
    DashboardProfilLossPerDayDTO toProfitLossPerDayDTO(ProfitLossPerDayProjection projection);
    DashboardHeatMapByYearDTO toHeatMapPerYearDTO(DashboardHeatMapByYearProjection projection);

    SummaryPerAfdelingDTO toSummaryPerAfdelingDTO(SummaryPerAfdelingProjection projection);
    SummaryPerContractorDTO toSummaryPerContractorDTO(SummaryPerContractorProjection projection);
    SummaryPerDriverDTO toSummaryPerDriverDTO(SummaryPerDriverProjection projection);
    SummaryPerMillDTO toSummaryPerMillDTO(SummaryPerMillProjection projection);
    SummaryPerVehicleDTO toSummaryPerVehicleDTO(SummaryPerVehicleProjection projection);

    SummaryPerTripTypeDTO toSummaryPerTripTypeDTO(SummaryPerTripTypeProjection projection);
}
