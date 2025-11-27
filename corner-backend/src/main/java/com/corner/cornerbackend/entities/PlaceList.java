package com.corner.cornerbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "place_lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    private String coverImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    @Column(nullable = false)
    private boolean isPublic = true;

    @OneToMany(mappedBy = "placeList", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SavedPlace> savedPlaces = new ArrayList<>();

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();
}
