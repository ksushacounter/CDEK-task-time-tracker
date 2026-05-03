package com.timetracker.task_time_tracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/auth/login") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid Authorization header", "Unauthorized");
            return;
        }

        final String jwt = authHeader.substring(7);

        if (jwt == null || jwt.trim().isEmpty()) {
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Empty JWT token", "Unauthorized");
            return;
        }

        try {
            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.isTokenValid(jwt, username)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, null);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid or expired JWT token", "Unauthorized");
                    return;
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "JWT token expired", "Unauthorized");

        } catch (MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid JWT token format", "Unauthorized");

        } catch (IllegalArgumentException e) {
            logger.error("Empty or malformed JWT token: {}", e.getMessage());
            sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Empty or malformed JWT token", "Unauthorized");

        } catch (Exception e) {
            logger.error("JWT authentication error: {}", e.getMessage());
            sendErrorResponse(response, request, HttpServletResponse.SC_FORBIDDEN,
                    "Authentication failed", "Forbidden");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpServletRequest request,
                                   int statusCode, String message, String error) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", statusCode);
        errorBody.put("error", error);
        errorBody.put("message", message);
        errorBody.put("path", request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}