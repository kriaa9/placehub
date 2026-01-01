package com.placehub.auth;

import com.placehub.exception.TooManyRequestsException;
import com.placehub.security.RateLimitingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RateLimitingService rateLimitingService;

    /**
     * Registers a new user.
     *
     * @param request        the registration request
     * @param servletRequest the HTTP servlet request for extracting device info
     * @return the authentication response with JWT tokens
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest servletRequest
    ) {
        String userAgent = servletRequest.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(servletRequest);

        AuthenticationResponse response = authenticationService.register(request, userAgent, ipAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param request        the authentication request
     * @param servletRequest the HTTP servlet request for extracting device info
     * @return the authentication response with JWT tokens
     * @throws TooManyRequestsException if rate limit is exceeded
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletRequest servletRequest
    ) {
        String ipAddress = getClientIpAddress(servletRequest);

        // Check rate limiting
        if (!rateLimitingService.isLoginAllowed(ipAddress)) {
            throw new TooManyRequestsException("Too many login attempts. Please try again in 15 minutes.");
        }

        String userAgent = servletRequest.getHeader("User-Agent");

        AuthenticationResponse response = authenticationService.authenticate(request, userAgent, ipAddress);
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out a user (client-side token deletion).
     * Since JWT is stateless, logout is handled on the client by deleting the token.
     *
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful. Please delete the token on client side.");
    }

    /**
     * Extracts the client IP address from the request, considering proxy headers.
     *
     * @param request the HTTP servlet request
     * @return the client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP in the list (original client IP)
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
