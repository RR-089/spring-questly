package com.personal.spring_questly.filter;

import com.personal.spring_questly.config.SecurityConfig;
import com.personal.spring_questly.exception.CustomException;
import com.personal.spring_questly.exception.CustomException.BadRequestException;
import com.personal.spring_questly.exception.CustomException.InternalServerErrorException;
import com.personal.spring_questly.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.personal.spring_questly.util.SendResponseUtil.sendCustomErrorResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] publicPathPatterns = SecurityConfig.publicPathPatterns;

        String currentPath =
                request.getRequestURI().substring(request.getContextPath().length());

        for (String pattern : publicPathPatterns) {
            if (antPathMatcher.match(pattern, currentPath)) {
                log.info("Skipping JWT authentication filter");
                return true;
            }
        }

        log.info("Executing JWT authentication filter");
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = validateAuthHeaderAndGetToken(request, response);
            String email = jwtUtil.extractEmail(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (CustomException ex) {
            sendCustomErrorResponse(ex, response);
            return;
        } catch (Exception e) {
            sendCustomErrorResponse(new InternalServerErrorException("Unknown error " +
                            "occurred at Jwt Authentication Filter", null),
                    response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String validateAuthHeaderAndGetToken(HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        String tokenType = "Bearer ";

        if (authHeader == null) {
            throw new BadRequestException("Authorization header is " +
                    "required", null);
        }

        if (!authHeader.startsWith(tokenType)) {
            throw new BadRequestException("Request must include a Bearer token.", null);
        }

        return authHeader.substring(tokenType.length());
    }
}
