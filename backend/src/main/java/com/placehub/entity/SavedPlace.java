package com.placehub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SavedPlace Entity - Links a Place to a PlaceList.
 */
@Entity
@Table(name = "saved_places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which list does this belong to?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_list_id", nullable = false)
    private PlaceList placeList;

    // Which place is being saved?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // Your personal note about this place
    @Column(length = 1000)
    private String note;

    // Custom image URL (optional)
    @Column(name = "image_url")
    private String imageUrl;

    // Tags (e.g., "vegan", "cheap", "romantic")
    @ElementCollection
    @CollectionTable(name = "saved_place_tags", joinColumns = @JoinColumn(name = "saved_place_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // Rating (1-5 stars)
    private Integer rating;

    // Did you visit this place?
    @Builder.Default
    private Boolean visited = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
