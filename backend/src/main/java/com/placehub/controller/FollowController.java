package com.placehub.controller;

import java.util.Map;

import com.placehub.DTO.UserSummaryDTO;
import com.placehub.service.FollowService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing follow relationships between users.
 */
@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * Follow a user.
     *
     * @param userId the ID of the user to follow
     * @return 200 OK on success
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, String>> followUser(@PathVariable Long userId) {
        followService.followUser(userId);
        return ResponseEntity.ok(Map.of("message", "Successfully followed user"));
    }

    /**
     * Unfollow a user.
     *
     * @param userId the ID of the user to unfollow
     * @return 200 OK on success
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> unfollowUser(@PathVariable Long userId) {
        followService.unfollowUser(userId);
        return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user"));
    }

    /**
     * Get followers of a user.
     *
     * @param userId the ID of the user
     * @param page   page number (0-indexed)
     * @param size   page size
     * @return paginated list of followers
     */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<Page<UserSummaryDTO>> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<UserSummaryDTO> followers = followService.getFollowers(userId, pageable);
        return ResponseEntity.ok(followers);
    }

    /**
     * Get users that a user is following.
     *
     * @param userId the ID of the user
     * @param page   page number (0-indexed)
     * @param size   page size
     * @return paginated list of following
     */
    @GetMapping("/following/{userId}")
    public ResponseEntity<Page<UserSummaryDTO>> getFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<UserSummaryDTO> following = followService.getFollowing(userId, pageable);
        return ResponseEntity.ok(following);
    }

    /**
     * Check if the current user is following a specific user.
     *
     * @param userId the ID of the user to check
     * @return JSON with isFollowing boolean
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Boolean>> getFollowStatus(@PathVariable Long userId) {
        boolean isFollowing = followService.isFollowing(userId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }
}
