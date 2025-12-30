package com.placehub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SavedPlace Entity - Links a Place to a PlaceList.
 */
@Document(collection = "saved_places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPlace {

    @Id
    private String id;

    // Which list does this belong to? (PlaceList ID)
    private String placeListId;

    // Which place is being saved? (Place ID)
    private String placeId;

    // Your personal note about this place
    private String note;

    // Custom image URL (optional)
    private String imageUrl;

    // Tags (e.g., "vegan", "cheap", "romantic")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // Rating (1-5 stars)
    private Integer rating;

    // Did you visit this place?
    @Builder.Default
    private Boolean visited = false;

    @CreatedDate
    private LocalDateTime createdAt;
}
