package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.LoginResponse;
import com.ecommerce.auth_service.dto.RegistrationRequest;
import com.ecommerce.auth_service.dto.RegistrationResponse;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.exception.InvalidCredentialsException;
import com.ecommerce.auth_service.exception.UserAlreadyExistsException;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegistrationResponse registerUser(RegistrationRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User already exists with email: " + request.getEmail());
                });

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        User savedUser = userRepository.save(user);

        return new RegistrationResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "user registered"
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser =
                userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = optionalUser.get();

        boolean isMatch = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!isMatch) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }

}
