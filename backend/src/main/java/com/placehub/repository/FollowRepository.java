package com.placehub.repository;

import java.util.Optional;

import com.placehub.entity.Follow;
import com.placehub.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Follow entity.
 * Manages follow relationships between users.
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * Check if a follow relationship exists between two users.
     *
     * @param follower  the user who is following
     * @param following the user being followed
     * @return true if the relationship exists
     */
    boolean existsByFollowerAndFollowing(User follower, User following);

    /**
     * Find a specific follow relationship.
     *
     * @param follower  the user who is following
     * @param following the user being followed
     * @return Optional containing the follow if found
     */
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    /**
     * Get all users that a specific user is following.
     *
     * @param follower the user whose following list to retrieve
     * @param pageable pagination information
     * @return page of follow relationships
     */
    Page<Follow> findByFollower(User follower, Pageable pageable);

    /**
     * Get all followers of a specific user.
     *
     * @param following the user whose followers to retrieve
     * @param pageable  pagination information
     * @return page of follow relationships
     */
    Page<Follow> findByFollowing(User following, Pageable pageable);

    /**
     * Count how many users a specific user is following.
     *
     * @param follower the user
     * @return count of users being followed
     */
    long countByFollower(User follower);

    /**
     * Count how many followers a specific user has.
     *
     * @param following the user
     * @return count of followers
     */
    long countByFollowing(User following);

    /**
     * Delete a follow relationship.
     *
     * @param follower  the user who is following
     * @param following the user being followed
     */
    void deleteByFollowerAndFollowing(User follower, User following);

    /**
     * Check if follower follows following by their IDs.
     *
     * @param followerId  the ID of the follower
     * @param followingId the ID of the user being followed
     * @return true if the relationship exists
     */
    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower.id = :followerId AND f.following.id = :followingId")
    boolean existsByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
}
