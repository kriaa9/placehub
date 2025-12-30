package com.placehub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PlaceList Entity - A collection of saved places.
 */
@Document(collection = "place_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceList {

    @Id
    private String id;

    @NotBlank(message = "List name is required")
    private String name;

    private String description;
    private String coverImageUrl;

    // Is this list visible to everyone?
    @Builder.Default
    private Boolean isPublic = false;

    // Who owns this list? (User ID)
    private String ownerId;

    // List of saved place IDs in this list
    @Builder.Default
    private List<String> savedPlaceIds = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
