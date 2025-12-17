package com.project.bbapalmchain.dto.projection;

public interface SummaryPerTripTypeProjection {
    Long getTripTypeId();
    String getTripTypeName();

    Long getTotalTrips();
    Double getTotalLoad();
    Double getTotalRevenue();
    Double getTotalContractorExpenses();
    Double getTotalFeeOperational();
    Double getTotalExpenses();
    Double getProfitLoss();
}
