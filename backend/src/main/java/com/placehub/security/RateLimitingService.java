package com.placehub.security;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

/**
 * Service for rate limiting login attempts using Bucket4j.
 * Limits to 5 login attempts per 15 minutes per IP address.
 */
@Service
public class RateLimitingService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(15);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Checks if a login attempt is allowed for the given IP address.
     *
     * @param ipAddress the client IP address
     * @return true if the attempt is allowed, false if rate limit exceeded
     */
    public boolean isLoginAllowed(String ipAddress) {
        Bucket bucket = buckets.computeIfAbsent(ipAddress, this::createBucket);
        return bucket.tryConsume(1);
    }

    /**
     * Creates a new rate limiting bucket for an IP address.
     *
     * @param ipAddress the client IP address
     * @return a new bucket with configured rate limits
     */
    private Bucket createBucket(String ipAddress) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(MAX_ATTEMPTS)
                .refillGreedy(MAX_ATTEMPTS, WINDOW_DURATION)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Gets the number of available tokens for an IP address.
     *
     * @param ipAddress the client IP address
     * @return the number of available tokens
     */
    public long getAvailableTokens(String ipAddress) {
        Bucket bucket = buckets.get(ipAddress);
        if (bucket == null) {
            return MAX_ATTEMPTS;
        }
        return bucket.getAvailableTokens();
    }
}
