package com.iqvia.quickfix.controller;

import com.iqvia.quickfix.dto.UserDtos;
import com.iqvia.quickfix.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDtos.UserResponse> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDtos.UserResponse getUserById(
            @PathVariable Long id
    ) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDtos.UserResponse createUser(
            @Valid @RequestBody UserDtos.CreateUserRequest request
    ) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserDtos.UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDtos.UpdateUserRequest request
    ) {
        return userService.updateUser(id, request);
    }

    @PutMapping("/{id}/reset-password")
    public UserDtos.UserResponse resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody UserDtos.ResetPasswordRequest request
    ) {
        return userService.resetPassword(id, request);
    }
}
