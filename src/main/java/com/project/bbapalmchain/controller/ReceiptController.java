package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.ReceiptBatchDTO;
import com.project.bbapalmchain.dto.ReceiptDTO;
import com.project.bbapalmchain.dto.ReceiptReqDTO;
import com.project.bbapalmchain.service.ReceiptService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/receipts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody ReceiptReqDTO dto) {
        final Long createdId = receiptService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createBulk(@RequestBody List<ReceiptReqDTO> dtoList) {
        return new ResponseEntity<>(receiptService.createBulk(dtoList), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody ReceiptReqDTO dto) {
        receiptService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ReceiptDTO>> getAll(@RequestParam(required = false) String startDate,
                                                   @RequestParam(required = false) String endDate,
                                                   @RequestParam(required = false) Long accountId) {
        List<ReceiptDTO> result = receiptService.getAll(accountId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-all-grouped")
    public ResponseEntity<List<ReceiptBatchDTO>> getAllGrouped(@RequestParam(required = false) String startDate,
                                                    @RequestParam(required = false) String endDate,
                                                    @RequestParam(required = false) Long accountId) {
        return ResponseEntity.ok(receiptService.getAllGrouped(accountId, startDate, endDate));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        receiptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
