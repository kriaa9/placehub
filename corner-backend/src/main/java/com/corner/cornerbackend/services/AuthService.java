package com.corner.cornerbackend.services;

import com.corner.cornerbackend.dto.AuthResponse;
import com.corner.cornerbackend.dto.LoginRequest;
import com.corner.cornerbackend.dto.RegisterRequest;
import com.corner.cornerbackend.entities.User;
import com.corner.cornerbackend.repositories.UserRepository;
import com.corner.cornerbackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;

        public AuthResponse register(RegisterRequest request) {
                if (!request.getPassword().equals(request.getConfirmPassword())) {
                        throw new IllegalArgumentException("Passwords do not match");
                }
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new IllegalArgumentException("Email already in use");
                }

                var user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .build();

                user = userRepository.save(user);

                var userDetails = new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPassword(),
                                java.util.Collections.emptyList());

                var jwtToken = jwtUtil.generateToken(userDetails);

                return AuthResponse.builder()
                                .accessToken(jwtToken)
                                .userId(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow();

                var userDetails = new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPassword(),
                                java.util.Collections.emptyList());

                var jwtToken = jwtUtil.generateToken(userDetails);

                return AuthResponse.builder()
                                .accessToken(jwtToken)
                                .userId(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .build();
        }
}
