package com.project.bbapalmchain.config;

import com.project.bbapalmchain.service.AuthService;
import com.project.bbapalmchain.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Jika tidak ada header Authorization atau tidak dimulai dengan "Bearer ",
        //    langsung lanjutkan ke filter berikutnya
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwt);

            // 2. Jika username ada dan belum ada autentikasi di SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.authService.loadUserByUsername(username);

                // 3. Jika token valid, set autentikasi di SecurityContext
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 4. Log error untuk debugging tapi JANGAN handle di sini
            //    Biarkan Spring Security yang menangani authentication failure
            logger.warn("Error processing JWT token: " + e.getMessage());
            // HAPUS BAGIAN INI: resolver.resolveException(request, response, null, e);
            // HAPUS BAGIAN INI: return;
        }

        // 5. SELALU lanjutkan ke filter berikutnya, biarkan Spring Security
        //    yang menangani authentication/authorization
        filterChain.doFilter(request, response);
    }
}