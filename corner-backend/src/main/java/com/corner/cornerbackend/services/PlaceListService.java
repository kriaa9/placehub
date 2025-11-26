package com.corner.cornerbackend.services;

import com.corner.cornerbackend.dto.*;
import com.corner.cornerbackend.entities.Place;
import com.corner.cornerbackend.entities.PlaceList;
import com.corner.cornerbackend.entities.SavedPlace;
import com.corner.cornerbackend.entities.User;
import com.corner.cornerbackend.repositories.PlaceListRepository;
import com.corner.cornerbackend.repositories.SavedPlaceRepository;
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
public class PlaceListService {

    private final PlaceListRepository placeListRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final UserRepository userRepository;
    private final PlaceService placeService;
    private final FollowService followService;

    @Transactional
    public PlaceListResponse createList(PlaceListRequest request) {
        User user = getCurrentUser();

        PlaceList placeList = PlaceList.builder()
                .name(request.getName())
                .description(request.getDescription())
                .coverImageUrl(request.getCoverImageUrl())
                .owner(user)
                .isPublic(request.isPublic())
                .build();

        placeList = placeListRepository.save(placeList);
        return mapToResponse(placeList);
    }

    public List<PlaceListResponse> getMyLists() {
        User user = getCurrentUser();
        return placeListRepository.findByOwner(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PlaceListResponse> getUserLists(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return only public lists
        return placeListRepository.findByOwnerAndIsPublicTrue(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PlaceListResponse getListById(Long listId) {
        PlaceList placeList = placeListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        // Check visibility
        User currentUser = getCurrentUserOptional();
        if (!placeList.isPublic()) {
            if (currentUser == null || !placeList.getOwner().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied to private list");
            }
        }

        return mapToResponse(placeList);
    }

    @Transactional
    public SavedPlaceResponse addPlaceToList(Long listId, SavedPlaceRequest request) {
        User user = getCurrentUser();
        PlaceList placeList = placeListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        if (!placeList.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You can only add places to your own lists");
        }

        // Get or create the global place
        Place place = placeService.getOrCreatePlace(request.getPlace());

        // Create saved place association
        SavedPlace savedPlace = SavedPlace.builder()
                .placeList(placeList)
                .place(place)
                .note(request.getNote())
                .imageUrl(request.getImageUrl())
                .tags(request.getTags())
                .build();

        savedPlace = savedPlaceRepository.save(savedPlace);
        return mapToSavedPlaceResponse(savedPlace);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private User getCurrentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    private PlaceListResponse mapToResponse(PlaceList list) {
        UserSummaryDto ownerSummary = UserSummaryDto.builder()
                .id(list.getOwner().getId())
                .firstName(list.getOwner().getFirstName())
                .lastName(list.getOwner().getLastName())
                .avatarUrl(list.getOwner().getAvatarUrl())
                .build();

        List<SavedPlaceResponse> places = savedPlaceRepository.findByPlaceList(list).stream()
                .map(this::mapToSavedPlaceResponse)
                .collect(Collectors.toList());

        return PlaceListResponse.builder()
                .id(list.getId())
                .name(list.getName())
                .description(list.getDescription())
                .coverImageUrl(list.getCoverImageUrl())
                .owner(ownerSummary)
                .isPublic(list.isPublic())
                .placesCount(places.size())
                .places(places)
                .createdAt(list.getCreatedAt())
                .updatedAt(list.getUpdatedAt())
                .build();
    }

    private SavedPlaceResponse mapToSavedPlaceResponse(SavedPlace savedPlace) {
        return SavedPlaceResponse.builder()
                .id(savedPlace.getId())
                .place(placeService.mapToResponse(savedPlace.getPlace()))
                .note(savedPlace.getNote())
                .imageUrl(savedPlace.getImageUrl())
                .tags(savedPlace.getTags())
                .createdAt(savedPlace.getCreatedAt())
                .build();
    }
}
