package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary-afdeling")
    public ResponseEntity<List<SummaryPerAfdelingDTO>> getSummaryPerAfdeling(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> afdelingIds) {
        return ResponseEntity.ok(reportService.getSummaryPerAfdeling(startDate, endDate, afdelingIds));
    }

    @GetMapping("/summary-contractor")
    public ResponseEntity<List<SummaryPerContractorDTO>> getSummaryPerContractor(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> contractorIds) {
        return ResponseEntity.ok(reportService.getSummaryPerContractor(startDate, endDate, contractorIds));
    }

    @GetMapping("/summary-driver")
    public ResponseEntity<List<SummaryPerDriverDTO>> getSummaryPerDriver(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> driverIds) {
        return ResponseEntity.ok(reportService.getSummaryPerDriver(startDate, endDate, driverIds));
    }

    @GetMapping("/summary-mill")
    public ResponseEntity<List<SummaryPerMillDTO>> getSummaryPerMill(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> millIds) {
        return ResponseEntity.ok(reportService.getSummaryPerMill(startDate, endDate, millIds));
    }

    @GetMapping("/summary-vehicle")
    public ResponseEntity<List<SummaryPerVehicleDTO>> getSummaryPerVehicle(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> vehicleIds) {
        return ResponseEntity.ok(reportService.getSummaryPerVehicle(startDate, endDate, vehicleIds));
    }

    @GetMapping("/summary-finance")
    public ResponseEntity<List<SummaryFinancePerCategoryDTO>> getSummaryFinance(
            @RequestParam String type,
            @RequestParam Long accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(reportService.getSummaryFinance(type, accountId, startDate, endDate));
    }

    @GetMapping("/summary-trip-types")
    public ResponseEntity<List<SummaryPerTripTypeDTO>> getSummaryPerTripType(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> tripTypeIds) {
        return ResponseEntity.ok(reportService.getSummaryPerTripType(startDate, endDate, tripTypeIds));
    }

}
