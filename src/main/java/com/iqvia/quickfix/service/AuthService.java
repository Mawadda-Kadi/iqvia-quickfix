package com.iqvia.quickfix.service;

import com.iqvia.quickfix.dto.AuthDtos;
import com.iqvia.quickfix.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userService.getUserEntityByUsername(request.username());

        String token = jwtService.generateTocken(user);

        return new AuthDtos.LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
