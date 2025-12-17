package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.DashboardHeatMapByYearDTO;
import com.project.bbapalmchain.dto.DashboardProfilLossPerDayDTO;
import com.project.bbapalmchain.dto.DashboardSummaryDTO;
import com.project.bbapalmchain.dto.FinanceDashboardSummaryDTO;
import com.project.bbapalmchain.service.FinancialService;
import com.project.bbapalmchain.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DashboardController {

    private final TripService tripService;
    private final FinancialService financialService;

    @GetMapping("/get-summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(tripService.getSummary(startDate, endDate));
    }

    @GetMapping("/get-summary-finance")
    public ResponseEntity<FinanceDashboardSummaryDTO> getSummaryFinance(
            @RequestParam Long accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(financialService.getSummary(accountId, startDate, endDate));
    }


    @GetMapping("/total-profit-loss-per-day")
    public ResponseEntity<List<DashboardProfilLossPerDayDTO>> getProfitLossPerDay(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(tripService.getProfitLossPerDay(startDate, endDate));
    }

    @GetMapping("/total-finance-profit-loss-per-day")
    public ResponseEntity<List<DashboardProfilLossPerDayDTO>> getFinanceProfitLossPerDay(
            @RequestParam Long accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(financialService.getProfitLossPerDay(accountId, startDate, endDate));
    }

    @GetMapping("/heat-map-per-year")
    public ResponseEntity<List<DashboardHeatMapByYearDTO>> getHeatMapPerYear(
            @RequestParam Integer year) {
        return ResponseEntity.ok(tripService.getHeatmap(year));
    }

}
