package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.ManualReconciliationRequestDTO;
import com.project.bbapalmchain.dto.ReconciliationRowDTO;
import com.project.bbapalmchain.service.ReconciliationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reconciliations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    @GetMapping
    public ResponseEntity<List<ReconciliationRowDTO>> getAll(
            @RequestParam Long accountId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        List<ReconciliationRowDTO> data = reconciliationService.getAll(accountId, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/manual")
    public ResponseEntity<String> manualReconcile(@RequestBody ManualReconciliationRequestDTO request) {
        try {
            reconciliationService.manualReconcile(request);
            return ResponseEntity.ok("Transaction reconciled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/auto")
    public ResponseEntity<String> autoReconcile(
            @RequestParam Long accountId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(reconciliationService.autoReconcile(accountId, startDate, endDate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> unreconcile(@PathVariable Long id) {
        try {
            reconciliationService.unreconcile(id);
            return ResponseEntity.ok("Reconciliation with ID " + id + " has been successfully undone.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
             log.error("Error during unreconcile for id {}: ", id, e); //
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

}
