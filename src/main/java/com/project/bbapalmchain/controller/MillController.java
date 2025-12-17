package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.MillRequestDTO;
import com.project.bbapalmchain.dto.MillResponseDTO;
import com.project.bbapalmchain.service.MillService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/mills", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MillController {

    private final MillService millService;

    @GetMapping
    public ResponseEntity<List<MillResponseDTO>> getAll() {
        return ResponseEntity.ok(millService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MillResponseDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(millService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody MillRequestDTO dto) {
        final Long createdId = millService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody MillRequestDTO dto) {
        millService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        millService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
