package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.UserRequestDTO;
import com.project.bbapalmchain.dto.UserResponseDTO;
import com.project.bbapalmchain.model.User;
import com.project.bbapalmchain.service.UserService;
import com.project.bbapalmchain.util.UserContext;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserContext userContext;


    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> create(@RequestBody UserRequestDTO dto) {
        final Long createdId = userService.create(dto);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable(name = "id") Long id,
                                       @RequestBody UserRequestDTO dto) {
        userService.update(id, dto);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserInfo() {
        User user = userContext.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/id")
    public ResponseEntity<?> getCurrentUserId() {
        Long userId = userContext.getUserId();
        return ResponseEntity.ok(userId);
    }

}
