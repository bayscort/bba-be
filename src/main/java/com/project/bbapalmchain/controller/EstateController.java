package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.EstateRequestDTO;
import com.project.bbapalmchain.dto.EstateResponseDTO;
import com.project.bbapalmchain.service.EstateService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/estates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EstateController {

    private final EstateService estateService;

    @GetMapping
    public ResponseEntity<List<EstateResponseDTO>> getAll() {
        return ResponseEntity.ok(estateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstateResponseDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(estateService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody EstateRequestDTO dto) {
        final Long createdId = estateService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody EstateRequestDTO dto) {
        estateService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        estateService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
