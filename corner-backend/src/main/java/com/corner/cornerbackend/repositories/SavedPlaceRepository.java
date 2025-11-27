package com.corner.cornerbackend.repositories;

import com.corner.cornerbackend.entities.SavedPlace;
import com.corner.cornerbackend.entities.PlaceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Long> {
    List<SavedPlace> findByPlaceList(PlaceList placeList);
}
