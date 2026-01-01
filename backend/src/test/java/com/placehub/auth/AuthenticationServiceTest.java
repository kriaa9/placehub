package com.placehub.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.placehub.entity.User;
import com.placehub.exception.DuplicateEmailException;
import com.placehub.exception.InvalidCredentialsException;
import com.placehub.exception.UserNotFoundException;
import com.placehub.repository.RefreshTokenRepository;
import com.placehub.repository.UserRepository;
import com.placehub.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for AuthenticationService.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password@123";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_USER_AGENT = "Mozilla/5.0";
    private static final String TEST_IP_ADDRESS = "192.168.1.1";
    private static final String TEST_ACCESS_TOKEN = "access-token";
    private static final String TEST_REFRESH_TOKEN = "refresh-token";

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        private RegisterRequest registerRequest;

        @BeforeEach
        void setUp() {
            registerRequest = RegisterRequest.builder()
                    .email(TEST_EMAIL)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .password(TEST_PASSWORD)
                    .confirmPassword(TEST_PASSWORD)
                    .build();
        }

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() {
            // Arrange
            when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(jwtService.generateToken(any(User.class))).thenReturn(TEST_ACCESS_TOKEN);
            when(jwtService.generateRefreshToken()).thenReturn(TEST_REFRESH_TOKEN);
            when(jwtService.getAccessTokenExpiration()).thenReturn(900000L);
            when(jwtService.getRefreshTokenExpiration()).thenReturn(604800000L);

            // Act
            AuthenticationResponse response = authenticationService.register(
                    registerRequest, TEST_USER_AGENT, TEST_IP_ADDRESS);

            // Assert
            assertNotNull(response);
            assertEquals(TEST_ACCESS_TOKEN, response.getAccessToken());
            assertEquals(TEST_REFRESH_TOKEN, response.getRefreshToken());
            assertEquals("Bearer", response.getTokenType());
            assertEquals(TEST_EMAIL, response.getEmail());
            assertEquals(1L, response.getUserId());

            verify(userRepository).existsByEmail(TEST_EMAIL);
            verify(userRepository).save(any(User.class));
            verify(refreshTokenRepository).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email already exists")
        void shouldThrowDuplicateEmailExceptionWhenEmailExists() {
            // Arrange
            when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

            // Act & Assert
            DuplicateEmailException exception = assertThrows(
                    DuplicateEmailException.class,
                    () -> authenticationService.register(registerRequest, TEST_USER_AGENT, TEST_IP_ADDRESS)
            );

            assertEquals("Email already registered", exception.getMessage());
            verify(userRepository).existsByEmail(TEST_EMAIL);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        private AuthenticationRequest loginRequest;
        private User existingUser;

        @BeforeEach
        void setUp() {
            loginRequest = AuthenticationRequest.builder()
                    .email(TEST_EMAIL)
                    .password(TEST_PASSWORD)
                    .build();

            existingUser = User.builder()
                    .id(1L)
                    .email(TEST_EMAIL)
                    .firstName(TEST_FIRST_NAME)
                    .lastName(TEST_LAST_NAME)
                    .password("encoded-password")
                    .build();
        }

        @Test
        @DisplayName("Should authenticate user successfully")
        void shouldAuthenticateUserSuccessfully() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
            when(jwtService.generateToken(existingUser)).thenReturn(TEST_ACCESS_TOKEN);
            when(jwtService.generateRefreshToken()).thenReturn(TEST_REFRESH_TOKEN);
            when(jwtService.getAccessTokenExpiration()).thenReturn(900000L);
            when(jwtService.getRefreshTokenExpiration()).thenReturn(604800000L);
            when(refreshTokenRepository.countActiveTokensByUser(existingUser)).thenReturn(0L);

            // Act
            AuthenticationResponse response = authenticationService.authenticate(
                    loginRequest, TEST_USER_AGENT, TEST_IP_ADDRESS);

            // Assert
            assertNotNull(response);
            assertEquals(TEST_ACCESS_TOKEN, response.getAccessToken());
            assertEquals(TEST_REFRESH_TOKEN, response.getRefreshToken());
            assertEquals("Bearer", response.getTokenType());
            assertEquals(TEST_EMAIL, response.getEmail());
            assertEquals(1L, response.getUserId());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(refreshTokenRepository).save(any());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

            // Act & Assert
            UserNotFoundException exception = assertThrows(
                    UserNotFoundException.class,
                    () -> authenticationService.authenticate(loginRequest, TEST_USER_AGENT, TEST_IP_ADDRESS)
            );

            assertTrue(exception.getMessage().contains(TEST_EMAIL));
            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when password is wrong")
        void shouldThrowInvalidCredentialsExceptionWhenPasswordWrong() {
            // Arrange
            when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            InvalidCredentialsException exception = assertThrows(
                    InvalidCredentialsException.class,
                    () -> authenticationService.authenticate(loginRequest, TEST_USER_AGENT, TEST_IP_ADDRESS)
            );

            assertEquals("Invalid email or password", exception.getMessage());
        }
    }
}
