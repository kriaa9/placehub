package com.placehub.repository;

import java.util.Optional;

import com.placehub.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by email address.
     *
     * @param email the user's email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Search for users by first name, last name, or email.
     * Excludes the current user from search results.
     *
     * @param query         the search query (should include % wildcards)
     * @param currentUserId the ID of the current user to exclude
     * @param pageable      pagination information
     * @return page of matching users
     */
    @Query("SELECT u FROM User u WHERE u.id != :currentUserId AND " +
           "(LOWER(u.firstName) LIKE :query OR LOWER(u.lastName) LIKE :query OR LOWER(u.email) LIKE :query)")
    Page<User> searchUsers(@Param("query") String query, @Param("currentUserId") Long currentUserId, Pageable pageable);
}
