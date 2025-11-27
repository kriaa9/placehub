package com.corner.cornerbackend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "places")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String googlePlaceId;

    @Column(nullable = false)
    private String name;

    private String address;

    private String category;

    private Double latitude;

    private Double longitude;

    private String websiteUrl;

    private String originalSourceLink;
}
