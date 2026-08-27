package com.iqvia.quickfix.dto;

import com.iqvia.quickfix.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDtos {

    public record CreateUserRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotNull Role role,
            boolean enabled
    ) {}

    public record UpdateUserRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotNull Role role,
            boolean enabled
    ) {}

    public record ResetPasswordRequest(
            @NotBlank String newPassword,
            @NotBlank String repeatPassword
    ) {}

    public record UserResponse(
            Long id,
            String username,
            String email,
            Role role,
            boolean enabled
    ) {}
}
