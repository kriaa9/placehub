package com.placehub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PlaceList Entity - A collection of saved places (like a playlist but for places).
 */
@Entity
@Table(name = "place_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "List name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    // Is this list visible to everyone?
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    // Who owns this list?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== Relationships ==========

    // Places saved in this list
    @OneToMany(mappedBy = "placeList", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SavedPlace> savedPlaces = new ArrayList<>();
}
