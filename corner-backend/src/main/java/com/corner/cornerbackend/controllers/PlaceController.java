package com.corner.cornerbackend.controllers;

import com.corner.cornerbackend.dto.PlaceRequest;
import com.corner.cornerbackend.dto.PlaceResponse;
import com.corner.cornerbackend.services.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping("/check")
    public ResponseEntity<PlaceResponse> getOrCreatePlace(@Valid @RequestBody PlaceRequest request) {
        return ResponseEntity.ok(placeService.mapToResponse(placeService.getOrCreatePlace(request)));
    }
}
