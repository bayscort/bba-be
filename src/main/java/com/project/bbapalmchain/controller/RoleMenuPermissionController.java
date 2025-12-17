package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.RoleDetailResponseDTO;
import com.project.bbapalmchain.dto.RoleMenuPermissionRequestDTO;
import com.project.bbapalmchain.dto.RoleMenuPermissionResponseDTO;
import com.project.bbapalmchain.dto.RoleMenuPermissionUpdateRequestDTO;
import com.project.bbapalmchain.service.RoleMenuPermissionService;
import com.project.bbapalmchain.service.RoleService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/role-menu-permissions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RoleMenuPermissionController {

    private final RoleMenuPermissionService roleMenuPermissionService;
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleMenuPermissionResponseDTO>> getAll() {
        return ResponseEntity.ok(roleMenuPermissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleMenuPermissionResponseDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(roleMenuPermissionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody RoleMenuPermissionRequestDTO dto) {
        final Long createdId = roleMenuPermissionService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        roleMenuPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Void> updateRoleMenuPermissions(
            @PathVariable Long roleId,
            @RequestBody RoleMenuPermissionUpdateRequestDTO request) {
        roleMenuPermissionService.updateRoleMenuPermissions(roleId, request);
        return ResponseEntity.noContent().build();
    }

}
