package com.placehub.controller;

import java.util.Map;

import com.placehub.DTO.DeleteAccountRequest;
import com.placehub.DTO.UpdateProfileRequest;
import com.placehub.DTO.UserProfileResponse;
import com.placehub.DTO.UserSummaryDTO;
import com.placehub.service.ProfileService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for user profile management.
 * Provides endpoints for viewing and updating user profiles.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Get the current authenticated user's profile.
     *
     * @return the user's profile with statistics
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        UserProfileResponse profile = profileService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * Update the current authenticated user's profile.
     *
     * @param request the profile update request
     * @return the updated profile
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updatedProfile = profileService.updateProfile(request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Get another user's profile by ID.
     * Returns public profile information including follow status.
     *
     * @param userId the ID of the user to view
     * @return the user's public profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse profile = profileService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Search for users by name or email.
     *
     * @param q    the search query
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return paginated list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserSummaryDTO>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100)); // Max 100 per page
        Page<UserSummaryDTO> results = profileService.searchUsers(q, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Delete the current authenticated user's account.
     * Requires password confirmation for security.
     *
     * @param request the delete account request with password confirmation
     * @return 200 OK on successful deletion
     */
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        profileService.deleteAccount(request.getPassword());
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}
