package com.project.bbapalmchain.controller;

import com.project.bbapalmchain.dto.LoginRequest;
import com.project.bbapalmchain.dto.LoginResponse;
import com.project.bbapalmchain.dto.MenuPermissionDTO;
import com.project.bbapalmchain.model.Role;
import com.project.bbapalmchain.model.RoleMenuPermission;
import com.project.bbapalmchain.model.User;
import com.project.bbapalmchain.repository.RoleMenuPermissionRepository;
import com.project.bbapalmchain.service.AuthService;
import com.project.bbapalmchain.service.UserService;
import com.project.bbapalmchain.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleMenuPermissionRepository roleMenuPermissionRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(403).body("Login failed: " + e.getMessage());
        }

        userService.updateLastLogin(request.getUsername());


        String token = jwtUtil.generateToken(request.getUsername());

        User user = authService.getUserWithRole(request.getUsername());
        Role role = user.getRole();
        Long estateId = user.getEstate() != null ? user.getEstate().getId() : null;

        Map<String, Set<String>> menuMap = new HashMap<>();

        List<RoleMenuPermission> perms = roleMenuPermissionRepository.findByRole(role);
        for (RoleMenuPermission p : perms) {
            String menuName = p.getMenu().getName();
            String permission = p.getPermission().getOperation().toString();

            menuMap.computeIfAbsent(menuName, k -> new HashSet<>()).add(permission);
        }

        List<MenuPermissionDTO> menuPermissionList = menuMap.entrySet().stream()
                .map(entry -> new MenuPermissionDTO(entry.getKey(), new ArrayList<>(entry.getValue())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(token, user.getName(), user.getUsername(), role.getName(), menuPermissionList, estateId));
    }

}

