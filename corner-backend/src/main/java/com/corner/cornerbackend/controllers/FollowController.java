package com.corner.cornerbackend.controllers;

import com.corner.cornerbackend.dto.FollowResponse;
import com.corner.cornerbackend.dto.UserSummaryDto;
import com.corner.cornerbackend.services.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<FollowResponse> followUser(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.followUser(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<FollowResponse> unfollowUser(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.unfollowUser(userId));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<UserSummaryDto>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<UserSummaryDto>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}
