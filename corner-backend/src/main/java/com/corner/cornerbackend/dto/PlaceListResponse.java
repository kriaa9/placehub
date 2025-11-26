package com.corner.cornerbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceListResponse {
    private Long id;
    private String name;
    private String description;
    private String coverImageUrl;
    private UserSummaryDto owner;
    private boolean isPublic;
    private int placesCount;
    private List<SavedPlaceResponse> places;
    private Instant createdAt;
    private Instant updatedAt;
}
