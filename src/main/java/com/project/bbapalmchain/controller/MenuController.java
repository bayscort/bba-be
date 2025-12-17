package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.MenuRequestDTO;
import com.project.bbapalmchain.dto.MenuResponseDTO;
import com.project.bbapalmchain.service.MenuService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/menus", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuResponseDTO>> getAll() {
        return ResponseEntity.ok(menuService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(menuService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody MenuRequestDTO dto) {
        final Long createdId = menuService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody MenuRequestDTO dto) {
        menuService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
