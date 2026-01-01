package com.placehub.security;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

/**
 * Service for rate limiting login attempts using Bucket4j.
 * Limits to 5 login attempts per 15 minutes per IP address.
 * Includes scheduled cleanup of expired buckets to prevent memory leaks.
 */
@Service
public class RateLimitingService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(15);

    private final Map<String, BucketEntry> buckets = new ConcurrentHashMap<>();

    /**
     * Checks if a login attempt is allowed for the given IP address.
     *
     * @param ipAddress the client IP address
     * @return true if the attempt is allowed, false if rate limit exceeded
     */
    public boolean isLoginAllowed(String ipAddress) {
        BucketEntry entry = buckets.computeIfAbsent(ipAddress, this::createBucketEntry);
        entry.updateLastAccessTime();
        return entry.getBucket().tryConsume(1);
    }

    /**
     * Creates a new bucket entry for an IP address.
     *
     * @param ipAddress the client IP address
     * @return a new bucket entry with configured rate limits
     */
    private BucketEntry createBucketEntry(String ipAddress) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(MAX_ATTEMPTS)
                .refillGreedy(MAX_ATTEMPTS, WINDOW_DURATION)
                .build();

        Bucket bucket = Bucket.builder()
                .addLimit(limit)
                .build();

        return new BucketEntry(bucket);
    }

    /**
     * Gets the number of available tokens for an IP address.
     *
     * @param ipAddress the client IP address
     * @return the number of available tokens
     */
    public long getAvailableTokens(String ipAddress) {
        BucketEntry entry = buckets.get(ipAddress);
        if (entry == null) {
            return MAX_ATTEMPTS;
        }
        return entry.getBucket().getAvailableTokens();
    }

    /**
     * Scheduled task to clean up expired bucket entries.
     * Runs every 30 minutes to remove buckets that haven't been accessed
     * for longer than the rate limit window (15 minutes).
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void cleanupExpiredBuckets() {
        long expirationThreshold = System.currentTimeMillis() - WINDOW_DURATION.toMillis();
        
        Iterator<Map.Entry<String, BucketEntry>> iterator = buckets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BucketEntry> entry = iterator.next();
            if (entry.getValue().getLastAccessTime() < expirationThreshold) {
                iterator.remove();
            }
        }
    }

    /**
     * Gets the current number of tracked IP addresses.
     * Useful for monitoring.
     *
     * @return the number of tracked IP addresses
     */
    public int getTrackedIpCount() {
        return buckets.size();
    }

    /**
     * Internal class to track bucket with last access time.
     */
    private static class BucketEntry {
        private final Bucket bucket;
        private volatile long lastAccessTime;

        public BucketEntry(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public Bucket getBucket() {
            return bucket;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
}
