package com.corner.cornerbackend.services;

import com.corner.cornerbackend.dto.PlaceRequest;
import com.corner.cornerbackend.dto.PlaceResponse;
import com.corner.cornerbackend.entities.Place;
import com.corner.cornerbackend.repositories.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public Place getOrCreatePlace(PlaceRequest request) {
        // Try to find by Google Place ID first if provided
        if (request.getGooglePlaceId() != null) {
            Optional<Place> existingPlace = placeRepository.findByGooglePlaceId(request.getGooglePlaceId());
            if (existingPlace.isPresent()) {
                return existingPlace.get();
            }
        }

        // Create new place
        Place place = Place.builder()
                .googlePlaceId(request.getGooglePlaceId())
                .name(request.getName())
                .address(request.getAddress())
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .websiteUrl(request.getWebsiteUrl())
                .originalSourceLink(request.getOriginalSourceLink())
                .build();

        return placeRepository.save(place);
    }

    public PlaceResponse mapToResponse(Place place) {
        return PlaceResponse.builder()
                .id(place.getId())
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .address(place.getAddress())
                .category(place.getCategory())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .websiteUrl(place.getWebsiteUrl())
                .originalSourceLink(place.getOriginalSourceLink())
                .build();
    }
}
