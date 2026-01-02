package com.placehub.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile information.
 * All fields are optional - only non-null fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Pattern(regexp = "^(https?://.*)?$", message = "Avatar URL must be a valid URL")
    private String avatarUrl;

    @Pattern(regexp = "^(\\+?[0-9\\s-]{7,20})?$", message = "Phone number format is invalid")
    private String phone;

    @Pattern(regexp = "^(https?://.*)?$", message = "URL must be a valid URL")
    private String url;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    // Home location coordinates
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double homeLatitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double homeLongitude;

    // Social media links with URL validation
    @Pattern(regexp = "^(https?://(www\\.)?instagram\\.com/.*)?$", message = "Invalid Instagram URL format")
    private String instagram;

    @Pattern(regexp = "^(https?://(www\\.)?facebook\\.com/.*)?$", message = "Invalid Facebook URL format")
    private String facebook;

    @Pattern(regexp = "^(https?://(www\\.)?linkedin\\.com/.*)?$", message = "Invalid LinkedIn URL format")
    private String linkedin;

    @Pattern(regexp = "^(https?://(www\\.)?tiktok\\.com/.*)?$", message = "Invalid TikTok URL format")
    private String tiktok;
}
