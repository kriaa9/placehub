package com.placehub.repository;

import com.placehub.entity.Place;
import com.placehub.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Place entity.
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    /**
     * Count the number of places created by a user.
     *
     * @param createdBy the user who created the places
     * @return count of places
     */
    long countByCreatedBy(User createdBy);
}
