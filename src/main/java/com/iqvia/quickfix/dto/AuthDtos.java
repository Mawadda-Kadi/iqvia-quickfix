package com.iqvia.quickfix.dto;

import com.iqvia.quickfix.entity.Role;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record LoginResponse(
            String token,
            Long userId,
            String username,
            Role role
    ) {}
}
