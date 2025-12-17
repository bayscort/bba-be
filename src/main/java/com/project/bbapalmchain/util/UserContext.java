package com.project.bbapalmchain.util;

import com.project.bbapalmchain.model.User;
import com.project.bbapalmchain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {

    private final AuthService authService;

    /**
     * Mendapatkan username dari token JWT yang sedang aktif
     */
    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Mendapatkan User lengkap dari username yang sedang login
     */
    public User getCurrentUser() {
        String username = getUsername();
        return authService.getUserWithRole(username);
    }

    /**
     * Mendapatkan Role dari User yang sedang login
     */
    public String getRole() {
        return getCurrentUser().getRole().getName();
    }

    /**
     * Mendapatkan ID user jika field `User.id` tersedia
     */
    public Long getUserId() {
        return getCurrentUser().getId();
    }

}
