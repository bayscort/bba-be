package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.ContractorDTO;
import com.project.bbapalmchain.dto.ContractorRequestDTO;
import com.project.bbapalmchain.service.ContractorService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/contractors", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ContractorController {

    private final ContractorService contractorService;

    @GetMapping
    public ResponseEntity<List<ContractorDTO>> getAll() {
        return ResponseEntity.ok(contractorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractorDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(contractorService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody ContractorRequestDTO dto) {
        final Long createdId = contractorService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody ContractorRequestDTO dto) {
        contractorService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        contractorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
