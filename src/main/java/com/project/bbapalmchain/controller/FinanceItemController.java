package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.FinanceItemDTO;
import com.project.bbapalmchain.service.FinanceItemService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/finance-items", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FinanceItemController {

    private final FinanceItemService financeItemService;

    @GetMapping
    public ResponseEntity<List<FinanceItemDTO>> getAll() {
        return ResponseEntity.ok(financeItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinanceItemDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(financeItemService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody FinanceItemDTO dto) {
        final Long createdId = financeItemService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody FinanceItemDTO dto) {
        financeItemService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        financeItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
