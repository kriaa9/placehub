package com.corner.cornerbackend.services;

import com.corner.cornerbackend.dto.UpdateProfileRequest;
import com.corner.cornerbackend.dto.UserProfileResponse;
import com.corner.cornerbackend.entities.User;
import com.corner.cornerbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final FollowService followService;

    public UserProfileResponse getMyProfile() {
        User user = getCurrentUser();
        return buildUserProfileResponse(user, false);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        // Update only non-null fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        user = userRepository.save(user);
        return buildUserProfileResponse(user, false);
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        User currentUser = getCurrentUser();
        boolean isFollowing = followService.isFollowing(currentUser.getId(), userId);

        return buildUserProfileResponse(user, isFollowing);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserProfileResponse buildUserProfileResponse(User user, boolean isFollowing) {
        long followersCount = followService.getFollowerCount(user.getId());
        long followingCount = followService.getFollowingCount(user.getId());

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .followersCount((int) followersCount)
                .followingCount((int) followingCount)
                .isFollowing(isFollowing)
                .build();
    }

    @Transactional
    public void deleteMyProfile() {
        User user = getCurrentUser();
        // Delete user - cascading delete should handle related entities if configured
        // correctly
        // If not, we might need to manually delete lists, places, etc.
        // For now, assuming basic delete is sufficient or cascade is set up in DB
        userRepository.delete(user);
    }
}
