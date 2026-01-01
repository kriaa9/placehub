package com.placehub.auth;

import java.time.LocalDateTime;

import com.placehub.entity.RefreshToken;
import com.placehub.entity.User;
import com.placehub.exception.DuplicateEmailException;
import com.placehub.exception.InvalidCredentialsException;
import com.placehub.exception.UserNotFoundException;
import com.placehub.repository.RefreshTokenRepository;
import com.placehub.repository.UserRepository;
import com.placehub.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling user authentication and registration.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final int MAX_REFRESH_TOKENS_PER_USER = 5;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     *
     * @param request   the registration request
     * @param userAgent the User-Agent header
     * @param ipAddress the client IP address
     * @return the authentication response with JWT tokens
     * @throws DuplicateEmailException if email already exists
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request, String userAgent, String ipAddress) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        // Create new user
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Save user to database
        var savedUser = userRepository.save(user);

        // Generate tokens
        return generateTokens(savedUser, userAgent, ipAddress);
    }

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * @param request   the authentication request
     * @param userAgent the User-Agent header
     * @param ipAddress the client IP address
     * @return the authentication response with JWT tokens
     * @throws UserNotFoundException       if user not found
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request, String userAgent, String ipAddress) {
        // Find user by email
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

        // Authenticate user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Revoke old refresh tokens if limit exceeded (keep only MAX - 1 to make room for new token)
        revokeExcessTokens(user);

        // Generate new tokens
        return generateTokens(user, userAgent, ipAddress);
    }

    /**
     * Generates access and refresh tokens for a user.
     *
     * @param user      the user
     * @param userAgent the User-Agent header
     * @param ipAddress the client IP address
     * @return the authentication response with tokens
     */
    private AuthenticationResponse generateTokens(User user, String userAgent, String ipAddress) {
        // Generate JWT access token
        var accessToken = jwtService.generateToken(user);

        // Generate refresh token
        var refreshTokenStr = jwtService.generateRefreshToken();

        // Calculate expiration time
        var refreshExpirationMs = jwtService.getRefreshTokenExpiration();
        var expiresAt = LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000);

        // Save refresh token to database
        var refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);

        // Return response
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    /**
     * Refreshes an access token using a valid refresh token and rotates it.
     */
    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest request, String userAgent, String ipAddress) {
        RefreshToken existing = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (Boolean.TRUE.equals(existing.getRevoked()) || existing.isExpired()) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        User user = existing.getUser();

        // Revoke old token and issue new one
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        var newAccessToken = jwtService.generateToken(user);
        var newRefreshTokenStr = jwtService.generateRefreshToken();
        var refreshExpirationMs = jwtService.getRefreshTokenExpiration();

        var newRefresh = RefreshToken.builder()
                .token(newRefreshTokenStr)
                .user(user)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .build();

        refreshTokenRepository.save(newRefresh);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    /**
     * Revokes a single refresh token (logout current session).
     */
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken()).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    /**
     * Revokes all refresh tokens for a user (logout all sessions).
     */
    @Transactional
    public void logoutAll(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::revokeAllTokensByUser);
    }

    /**
     * Revokes excess refresh tokens if the user has more than the maximum allowed.
     * Uses a single database query for better performance.
     *
     * @param user the user
     */
    private void revokeExcessTokens(User user) {
        long activeTokenCount = refreshTokenRepository.countActiveTokensByUser(user);

        if (activeTokenCount >= MAX_REFRESH_TOKENS_PER_USER) {
            // Revoke oldest tokens in a single query, keeping only (MAX - 1) newest tokens
            // to make room for the new token about to be created
            refreshTokenRepository.revokeOldestTokens(user, MAX_REFRESH_TOKENS_PER_USER - 1);
        }
    }
}
