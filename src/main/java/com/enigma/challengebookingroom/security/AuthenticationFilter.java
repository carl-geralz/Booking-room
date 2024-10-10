package com.enigma.challengebookingroom.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.enigma.challengebookingroom.dto.response.JWTClaims;
import com.enigma.challengebookingroom.entity.User;
import com.enigma.challengebookingroom.service.JwtService;
import com.enigma.challengebookingroom.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final String AUTH_HEADER = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    )
            throws ServletException, IOException {
        try {
            String token = request.getHeader(AUTH_HEADER);
            if (Objects.nonNull(token) && jwtService.verifyToken(token)) {
                JWTClaims jwtClaims = jwtService.claimToken(token);
                User userByID = userService.getById(jwtClaims.getIdUser());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userByID.getUsername(),
                        null,
                        userByID.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        } catch (Throwable throwable) {
            log.error("Cannot set user authentication: {}", throwable.getLocalizedMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
