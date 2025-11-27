package com.corner.cornerbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedPlaceRequest {

    @NotNull(message = "Place details are required")
    private PlaceRequest place;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;

    private String imageUrl;

    private List<String> tags;
}
