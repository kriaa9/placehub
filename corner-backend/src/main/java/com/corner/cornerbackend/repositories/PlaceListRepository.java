package com.corner.cornerbackend.repositories;

import com.corner.cornerbackend.entities.PlaceList;
import com.corner.cornerbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceListRepository extends JpaRepository<PlaceList, Long> {
    List<PlaceList> findByOwner(User owner);

    List<PlaceList> findByOwnerAndIsPublicTrue(User owner);

    List<PlaceList> findByIsPublicTrue();
}
