package com.placehub.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Place Entity - Represents a location (restaurant, park, etc.).
 */
@Document(collection = "places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    private String id;

    @NotBlank(message = "Place name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String formattedAddress;

    // Google Places ID (if imported from Google)
    private String googlePlaceId;

    // Where did this place come from?
    @Builder.Default
    private PlaceSource source = PlaceSource.USER;

    // Who created this place? (User ID)
    private String createdById;

    @CreatedDate
    private LocalDateTime createdAt;
}
