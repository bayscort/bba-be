package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.TripTypeDTO;
import com.project.bbapalmchain.dto.TripTypeRequestDTO;
import com.project.bbapalmchain.service.TripTypeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/tripTypes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TripTypeController {

    private final TripTypeService tripTypeService;

    @GetMapping
    public ResponseEntity<List<TripTypeDTO>> getAll() {
        return ResponseEntity.ok(tripTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripTypeDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(tripTypeService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody TripTypeRequestDTO dto) {
        final Long createdId = tripTypeService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody TripTypeRequestDTO dto) {
        tripTypeService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        tripTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

