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
public class SavedPlaceResponse {
    private Long id;
    private PlaceResponse place;
    private String note;
    private String imageUrl;
    private List<String> tags;
    private Instant createdAt;
}
