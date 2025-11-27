package com.corner.cornerbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saved_places")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_list_id", nullable = false)
    private PlaceList placeList;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(length = 500)
    private String note;

    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "saved_place_tags", joinColumns = @JoinColumn(name = "saved_place_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private Instant createdAt = Instant.now();
}
