package com.corner.cornerbackend.controllers;

import com.corner.cornerbackend.dto.UpdateProfileRequest;
import com.corner.cornerbackend.dto.UserProfileResponse;
import com.corner.cornerbackend.services.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateMyProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getUserProfile(id));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile() {
        profileService.deleteMyProfile();
        return ResponseEntity.noContent().build();
    }
}
