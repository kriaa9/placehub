package com.placehub.service;

import com.placehub.DTO.UserSummaryDTO;
import com.placehub.entity.Follow;
import com.placehub.entity.User;
import com.placehub.exception.AlreadyFollowingException;
import com.placehub.exception.NotFollowingException;
import com.placehub.exception.ResourceNotFoundException;
import com.placehub.exception.SelfFollowException;
import com.placehub.repository.FollowRepository;
import com.placehub.repository.PlaceListRepository;
import com.placehub.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing follow relationships between users.
 */
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final PlaceListRepository placeListRepository;

    /**
     * Get the currently authenticated user.
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Follow a user.
     *
     * @param userId the ID of the user to follow
     * @throws SelfFollowException        if trying to follow self
     * @throws AlreadyFollowingException  if already following the user
     * @throws ResourceNotFoundException  if user not found
     */
    @Transactional
    public void followUser(Long userId) {
        User currentUser = getCurrentUser();

        // Prevent self-follow
        if (currentUser.getId().equals(userId)) {
            throw new SelfFollowException();
        }

        User userToFollow = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if already following
        if (followRepository.existsByFollowerAndFollowing(currentUser, userToFollow)) {
            throw new AlreadyFollowingException(currentUser.getId(), userId);
        }

        // Create follow relationship
        Follow follow = Follow.builder()
                .follower(currentUser)
                .following(userToFollow)
                .build();

        followRepository.save(follow);
    }

    /**
     * Unfollow a user.
     *
     * @param userId the ID of the user to unfollow
     * @throws NotFollowingException     if not following the user
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void unfollowUser(Long userId) {
        User currentUser = getCurrentUser();

        User userToUnfollow = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if following
        Follow follow = followRepository.findByFollowerAndFollowing(currentUser, userToUnfollow)
                .orElseThrow(() -> new NotFollowingException(currentUser.getId(), userId));

        followRepository.delete(follow);
    }

    /**
     * Get followers of a user.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return page of user summaries
     */
    @Transactional(readOnly = true)
    public Page<UserSummaryDTO> getFollowers(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = getCurrentUser();

        Page<Follow> follows = followRepository.findByFollowing(user, pageable);

        return follows.map(follow -> mapToUserSummary(follow.getFollower(), currentUser));
    }

    /**
     * Get users that a user is following.
     *
     * @param userId   the ID of the user
     * @param pageable pagination information
     * @return page of user summaries
     */
    @Transactional(readOnly = true)
    public Page<UserSummaryDTO> getFollowing(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = getCurrentUser();

        Page<Follow> follows = followRepository.findByFollower(user, pageable);

        return follows.map(follow -> mapToUserSummary(follow.getFollowing(), currentUser));
    }

    /**
     * Check if the current user is following a specific user.
     *
     * @param userId the ID of the user to check
     * @return true if following, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long userId) {
        User currentUser = getCurrentUser();
        return followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), userId);
    }

    /**
     * Map a User entity to UserSummaryDTO.
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
