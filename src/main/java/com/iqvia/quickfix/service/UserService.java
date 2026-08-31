package com.iqvia.quickfix.service;

import com.iqvia.quickfix.dto.UserDtos;
import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.exception.UserNotFoundException;
import com.iqvia.quickfix.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    // ----------- Find All Users

    public List<UserDtos.UserResponse> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toUserResponse)
                .toList();
    }

    // ------------- Get The User By ID

    public UserDtos.UserResponse getUserById(Long id) {
        User user = getUserEntityById(id);
        return toUserResponse(user);
    }

    // ---------------- Die Hilfsmethode _______________

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private UserDtos.UserResponse toUserResponse(User user) {

        return new UserDtos.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );
    }
}






