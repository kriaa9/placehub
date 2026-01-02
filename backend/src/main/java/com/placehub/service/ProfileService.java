package com.placehub.service;

import java.util.UUID;

import com.placehub.DTO.UpdateProfileRequest;
import com.placehub.DTO.UserProfileResponse;
import com.placehub.DTO.UserSummaryDTO;
import com.placehub.entity.User;
import com.placehub.exception.InvalidCredentialsException;
import com.placehub.exception.ResourceNotFoundException;
import com.placehub.repository.FollowRepository;
import com.placehub.repository.PlaceListRepository;
import com.placehub.repository.PlaceRepository;
import com.placehub.repository.RefreshTokenRepository;
import com.placehub.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing user profiles.
 * Handles profile retrieval, updates, and user search functionality.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PlaceRepository placeRepository;
    private final PlaceListRepository placeListRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get the currently authenticated user from the security context.
     *
     * @return the current user
     * @throws ResourceNotFoundException if user not found
     */
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Get the current user's profile with all statistics.
     *
     * @return the user's profile response
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return mapToProfileResponse(user, null);
    }

    /**
     * Get another user's profile by ID.
     * Applies privacy controls to hide sensitive information.
     *
     * @param userId the ID of the user to retrieve
     * @return the user's profile response with privacy controls applied
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = getCurrentUser();
        Boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), userId);

        // Apply privacy controls - hide sensitive data for public profiles
        return mapToPublicProfileResponse(user, isFollowing);
    }

    /**
     * Update the current user's profile.
     *
     * @param request the update request containing fields to update
     * @return the updated profile response
     */
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
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
        if (request.getUrl() != null) {
            user.setUrl(request.getUrl());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        // Coordinates
        if (request.getHomeLatitude() != null) {
            user.setHomeLatitude(request.getHomeLatitude());
        }
        if (request.getHomeLongitude() != null) {
            user.setHomeLongitude(request.getHomeLongitude());
        }
        // Social media links
        if (request.getInstagram() != null) {
            user.setInstagram(request.getInstagram());
        }
        if (request.getFacebook() != null) {
            user.setFacebook(request.getFacebook());
        }
        if (request.getLinkedin() != null) {
            user.setLinkedin(request.getLinkedin());
        }
        if (request.getTiktok() != null) {
            user.setTiktok(request.getTiktok());
        }

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser, null);
    }

    /**
     * Search for users by name or email.
     *
     * @param query    the search query
     * @param pageable pagination information
     * @return page of user summaries
     */
    @Transactional(readOnly = true)
    public Page<UserSummaryDTO> searchUsers(String query, Pageable pageable) {
        User currentUser = getCurrentUser();
        String searchQuery = "%" + query.toLowerCase() + "%";

        // Search in first name, last name, or email (case-insensitive)
        Page<User> users = userRepository.searchUsers(searchQuery, currentUser.getId(), pageable);

        return users.map(user -> mapToUserSummary(user, currentUser));
    }

    /**
     * Delete the current user's account after password verification.
     * Performs soft delete by setting account status to DELETED and anonymizing data.
     *
     * @param password the user's password for confirmation
     * @throws InvalidCredentialsException if password is incorrect
     */
    @Transactional
    public void deleteAccount(String password) {
        User user = getCurrentUser();

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllTokensByUser(user);

        // Soft delete: anonymize user data and set status to DELETED
        user.setAccountStatus(User.AccountStatus.DELETED);
        user.setEmail("deleted_" + UUID.randomUUID() + "@deleted.placehub.com");
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setBio(null);
        user.setAvatarUrl(null);
        user.setPhone(null);
        user.setUrl(null);
        user.setAddress(null);
        user.setHomeLatitude(null);
        user.setHomeLongitude(null);
        user.setInstagram(null);
        user.setFacebook(null);
        user.setLinkedin(null);
        user.setTiktok(null);

        userRepository.save(user);
    }

    /**
     * Get user statistics.
     *
     * @param user the user
     * @return array of [followersCount, followingCount, placesCount, listsCount]
     */
    private long[] getUserStatistics(User user) {
        long followersCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);
        long placesCount = placeRepository.countByCreatedBy(user);
        long listsCount = placeListRepository.countByOwner(user);

        return new long[]{followersCount, followingCount, placesCount, listsCount};
    }

    /**
     * Map a User entity to UserProfileResponse DTO.
     *
     * @param user        the user entity
     * @param isFollowing whether the current user follows this user (null for own profile)
     * @return the profile response
     */
    private UserProfileResponse mapToProfileResponse(User user, Boolean isFollowing) {
        long[] stats = getUserStatistics(user);

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .url(user.getUrl())
                .city(user.getCity())
                .country(user.getCountry())
                .address(user.getAddress())
                .homeLatitude(user.getHomeLatitude())
                .homeLongitude(user.getHomeLongitude())
                .instagram(user.getInstagram())
                .facebook(user.getFacebook())
                .linkedin(user.getLinkedin())
                .tiktok(user.getTiktok())
                .followersCount(stats[0])
                .followingCount(stats[1])
                .placesCount(stats[2])
                .listsCount(stats[3])
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isFollowing(isFollowing)
                .build();
    }

    /**
     * Map a User entity to UserProfileResponse DTO with privacy controls.
     * Hides email, phone, and exact address from public profiles.
     *
     * @param user        the user entity
     * @param isFollowing whether the current user follows this user
     * @return the profile response with sensitive data hidden
     */
    private UserProfileResponse mapToPublicProfileResponse(User user, Boolean isFollowing) {
        long[] stats = getUserStatistics(user);

        return UserProfileResponse.builder()
                .id(user.getId())
                // Email is hidden from public profiles
                .email(null)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                // Phone is hidden from public profiles
                .phone(null)
                .url(user.getUrl())
                .city(user.getCity())
                .country(user.getCountry())
                // Exact address is hidden from public profiles
                .address(null)
                // Exact coordinates are hidden from public profiles
                .homeLatitude(null)
                .homeLongitude(null)
                // Social media links are public
                .instagram(user.getInstagram())
                .facebook(user.getFacebook())
                .linkedin(user.getLinkedin())
                .tiktok(user.getTiktok())
                .followersCount(stats[0])
                .followingCount(stats[1])
                .placesCount(stats[2])
                .listsCount(stats[3])
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isFollowing(isFollowing)
                .build();
    }

    /**
     * Map a User entity to UserSummaryDTO.
     *
     * @param user        the user entity
     * @param currentUser the current user (for follow status)
     * @return the user summary
     */
    private UserSummaryDTO mapToUserSummary(User user, User currentUser) {
        boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), user.getId());
        long followersCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);
        long publicListsCount = placeListRepository.countPublicListsByOwnerId(user.getId());

        return UserSummaryDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .city(user.getCity())
                .country(user.getCountry())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .publicListsCount(publicListsCount)
                .isFollowing(isFollowing)
                .build();
    }
}
