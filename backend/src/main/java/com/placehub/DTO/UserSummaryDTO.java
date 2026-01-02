package com.placehub.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying user summary information in lists (e.g., search results, followers list).
 * Contains minimal public information suitable for displaying other users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String bio;
    private String city;
    private String country;

    // Statistics
    private long followersCount;
    private long followingCount;
    private long publicListsCount;

    // Relationship status with current user
    private Boolean isFollowing;

    /**
     * Helper method to get full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
