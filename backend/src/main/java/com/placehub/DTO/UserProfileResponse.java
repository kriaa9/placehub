package com.placehub.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning complete user profile data.
 * Used for GET /api/profile/me and GET /api/profile/{userId} endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private String phone;
    private String url;
    private String city;
    private String country;
    private String address;

    // Home location coordinates
    private Double homeLatitude;
    private Double homeLongitude;

    // Social media links
    private String instagram;
    private String facebook;
    private String linkedin;
    private String tiktok;

    // Statistics
    private long followersCount;
    private long followingCount;
    private long placesCount;
    private long listsCount;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For viewing other users' profiles - indicates if current user follows them
    private Boolean isFollowing;
}
