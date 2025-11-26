package com.corner.cornerbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceListRequest {

    @NotBlank(message = "List name is required")
    @Size(max = 100, message = "List name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private String coverImageUrl;

    @Builder.Default
    private boolean isPublic = true;
}
