package com.iqvia.quickfix.service;

import com.iqvia.quickfix.dto.UserDtos;
import com.iqvia.quickfix.entity.User;
import com.iqvia.quickfix.exception.EmailAlreadyExistsException;
import com.iqvia.quickfix.exception.PasswordsDoNotMatchException;
import com.iqvia.quickfix.exception.UserNotFoundException;
import com.iqvia.quickfix.exception.UsernameAlreadyExistsException;
import com.iqvia.quickfix.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    // ------------- Create User

    public UserDtos.UserResponse createUser(
            UserDtos.CreateUserRequest request
    ) {

        User user = new User();

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setEnabled(request.enabled());

        User saved = userRepository.save(user);

        return toUserResponse(saved);
    }

    // -------------- Update User

    public UserDtos.UserResponse updateUser(
            Long id,
            UserDtos.UpdateUserRequest request
    ) {
        User user = getUserEntityById(id);

        if (userRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailAlreadyExistsException(request.email());
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setEnabled(request.enabled());

        User saved = userRepository.save(user);

        return toUserResponse(saved);
    }

    // ------------- Reset Password

    public UserDtos.UserResponse resetPassword(
            Long id,
            UserDtos.ResetPasswordRequest request
    ) {
        User user = getUserEntityById(id);

        if (!request.newPassword().equals(request.repeatPassword())) {
            throw new PasswordsDoNotMatchException();
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));

        User saved = userRepository.save(user);

        return toUserResponse(saved);

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






