package com.placehub.entity;

/**
 * Enum representing the source of a place.
 */
public enum PlaceSource {
    USER,           // Manually created by a user
    GOOGLE_PLACES,  // Imported from Google Places API
    IMPORTED        // Imported from other sources
}
