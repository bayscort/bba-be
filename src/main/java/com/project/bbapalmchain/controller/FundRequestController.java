package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.*;
import com.project.bbapalmchain.service.FundRequestApprovalLogService;
import com.project.bbapalmchain.service.FundRequestService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/fund-requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FundRequestController {

    private final FundRequestService fundRequestService;

    private final FundRequestApprovalLogService fundRequestApprovalLogService;


    @GetMapping
    public ResponseEntity<List<FundRequestRespDTO>> findAll() {
        return ResponseEntity.ok(fundRequestService.findAll());
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<FundRequestRespDTO>> getAll(@RequestParam(required = false) String startDate,
                                                           @RequestParam(required = false) String endDate) {
        List<FundRequestRespDTO> result = fundRequestService.getAll(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FundRequestRespDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(fundRequestService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody FundRequestDTO dto) {
        final Long createdId = fundRequestService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody FundRequestDTO dto) {
        fundRequestService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/approval-log")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Void> createApprovalLog(@RequestBody FundRequestApprovalLogReqDTO dto) {
        fundRequestApprovalLogService.create(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-by-approval-role")
    public ResponseEntity<List<FundRequestRespDTO>> getByApprovalRole(@RequestParam String roleName) {

        List<FundRequestRespDTO> result = fundRequestService.getByApprovalRole(roleName);
        return ResponseEntity.ok(result);
    }

}
