package com.corner.cornerbackend.services;

import com.corner.cornerbackend.dto.FollowResponse;
import com.corner.cornerbackend.dto.UserSummaryDto;
import com.corner.cornerbackend.entities.Follow;
import com.corner.cornerbackend.entities.User;
import com.corner.cornerbackend.repositories.FollowRepository;
import com.corner.cornerbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponse followUser(Long targetUserId) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + targetUserId));

        // Prevent self-follow
        if (currentUser.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        // Check if already following
        if (followRepository.existsByFollowerAndFollowing(currentUser, targetUser)) {
            throw new IllegalArgumentException("Already following this user");
        }

        // Create follow relationship
        Follow follow = Follow.builder()
                .id(new Follow.FollowId(currentUser.getId(), targetUser.getId()))
                .follower(currentUser)
                .following(targetUser)
                .build();

        followRepository.save(follow);

        long followersCount = followRepository.countByFollowing(targetUser);

        return FollowResponse.builder()
                .userId(targetUserId)
                .isFollowing(true)
                .followersCount(followersCount)
                .message("Successfully followed user")
                .build();
    }

        @Transactional
    public FollowResponse unfollowUser(Long targetUserId) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + targetUserId));

        // Check if not following
        if (!followRepository.existsByFollowerAndFollowing(currentUser, targetUser)) {
            throw new IllegalArgumentException("Not following this user");
        }

        // Delete follow relationship
        followRepository.deleteByFollowerAndFollowing(currentUser, targetUser);

        long followersCount = followRepository.countByFollowing(targetUser);

        return FollowResponse.builder()
                .userId(targetUserId)
                .isFollowing(false)
                .followersCount(followersCount)
                .message("Successfully unfollowed user")
                .build();
    }

    public List<UserSummaryDto> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        User currentUser = getCurrentUser();

        List<Follow> followers = followRepository.findByFollowing(user);

        return followers.stream()
                .map(follow -> buildUserSummary(follow.getFollower(), currentUser))
                .collect(Collectors.toList());
    }

    public List<UserSummaryDto> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        User currentUser = getCurrentUser();

        List<Follow> following = followRepository.findByFollower(user);

        return following.stream()
                .map(follow -> buildUserSummary(follow.getFollowing(), currentUser))
                .collect(Collectors.toList());
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Following user not found"));

        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    public long getFollowerCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return followRepository.countByFollowing(user);
    }

    public long getFollowingCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return followRepository.countByFollower(user);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserSummaryDto buildUserSummary(User user, User currentUser) {
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(currentUser, user);

        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .isFollowing(isFollowing)
                .build();
    }
}
