package com.corner.cornerbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {
    private Long id;
    private String googlePlaceId;
    private String name;
    private String address;
    private String category;
    private Double latitude;
    private Double longitude;
    private String websiteUrl;
    private String originalSourceLink;
}
