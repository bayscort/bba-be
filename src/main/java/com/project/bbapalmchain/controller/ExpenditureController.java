package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.ExpenditureBatchDTO;
import com.project.bbapalmchain.dto.ExpenditureDTO;
import com.project.bbapalmchain.dto.ExpenditureReqDTO;
import com.project.bbapalmchain.service.ExpenditureService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/expenditures", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody ExpenditureReqDTO dto) {
        return new ResponseEntity<>(expenditureService.create(dto), HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createBulk(@RequestBody List<ExpenditureReqDTO> dtoList) {
        return new ResponseEntity<>(expenditureService.createBulk(dtoList), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody ExpenditureReqDTO dto) {
        expenditureService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ExpenditureDTO>> getAll(@RequestParam(required = false) String startDate,
                                                       @RequestParam(required = false) String endDate,
                                                       @RequestParam(required = false) Long accountId) {
        List<ExpenditureDTO> result = expenditureService.getAll(accountId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-all-grouped")
    public ResponseEntity<List<ExpenditureBatchDTO>> getAllGrouped(@RequestParam(required = false) String startDate,
                                                                   @RequestParam(required = false) String endDate,
                                                                   @RequestParam(required = false) Long accountId) {
        return ResponseEntity.ok(expenditureService.getAllGrouped(accountId, startDate, endDate));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        expenditureService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
