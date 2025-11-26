package com.corner.cornerbackend.controllers;

import com.corner.cornerbackend.dto.PlaceListRequest;
import com.corner.cornerbackend.dto.PlaceListResponse;
import com.corner.cornerbackend.dto.SavedPlaceRequest;
import com.corner.cornerbackend.dto.SavedPlaceResponse;
import com.corner.cornerbackend.services.PlaceListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class PlaceListController {

    private final PlaceListService placeListService;

    @PostMapping
    public ResponseEntity<PlaceListResponse> createList(@Valid @RequestBody PlaceListRequest request) {
        return ResponseEntity.ok(placeListService.createList(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaceListResponse>> getMyLists() {
        return ResponseEntity.ok(placeListService.getMyLists());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaceListResponse>> getUserLists(@PathVariable Long userId) {
        return ResponseEntity.ok(placeListService.getUserLists(userId));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<PlaceListResponse> getListById(@PathVariable Long listId) {
        return ResponseEntity.ok(placeListService.getListById(listId));
    }

    @PostMapping("/{listId}/places")
    public ResponseEntity<SavedPlaceResponse> addPlaceToList(
            @PathVariable Long listId,
            @Valid @RequestBody SavedPlaceRequest request) {
        return ResponseEntity.ok(placeListService.addPlaceToList(listId, request));
    }
}
