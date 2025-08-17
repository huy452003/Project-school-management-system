package com.security.config;

import com.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    JwtService jwtService;
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        log.debug("Processing request: {}", request.getRequestURI());
        
        String authHeader = request.getHeader("Authorization");
        String jwt;
        String username;

        if (isPublicEndpoint(request.getRequestURI())) {
            log.debug("Skipping JWT validation for public endpoint: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Authorization header or not Bearer token for: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);
            log.debug("Extracted username from JWT: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if(jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Successfully authenticated user: {}", username);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage(), e);
            // Clear security context on error
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/auth/") || 
               uri.startsWith("/public/");
    }
}
