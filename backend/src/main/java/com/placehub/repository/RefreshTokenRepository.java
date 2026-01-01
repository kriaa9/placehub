package com.placehub.repository;

import java.util.List;
import java.util.Optional;

import com.placehub.entity.RefreshToken;
import com.placehub.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for RefreshToken entity.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find a refresh token by its token string.
     *
     * @param token the token string
     * @return Optional containing the refresh token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all active (non-revoked) refresh tokens for a user.
     *
     * @param user the user
     * @return list of active refresh tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false ORDER BY rt.createdAt DESC")
    List<RefreshToken> findActiveTokensByUser(@Param("user") User user);

    /**
     * Count active refresh tokens for a user.
     *
     * @param user the user
     * @return count of active tokens
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false")
    long countActiveTokensByUser(@Param("user") User user);

    /**
     * Revoke all refresh tokens for a user.
     *
     * @param user the user
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllTokensByUser(@Param("user") User user);

    /**
     * Revoke oldest active tokens for a user, keeping a specified number of newest tokens.
     *
     * @param user         the user
     * @param tokensToKeep the number of newest tokens to keep active
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.revoked = false AND rt.id NOT IN " +
           "(SELECT rt2.id FROM RefreshToken rt2 WHERE rt2.user = :user AND rt2.revoked = false ORDER BY rt2.createdAt DESC LIMIT :tokensToKeep)")
    void revokeOldestTokens(@Param("user") User user, @Param("tokensToKeep") int tokensToKeep);

    /**
     * Delete expired tokens.
     *
     * @return number of deleted tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < CURRENT_TIMESTAMP")
    int deleteExpiredTokens();
}
