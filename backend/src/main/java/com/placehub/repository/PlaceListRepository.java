package com.placehub.repository;

import com.placehub.entity.PlaceList;
import com.placehub.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PlaceList entity.
 */
@Repository
public interface PlaceListRepository extends JpaRepository<PlaceList, Long> {

    /**
     * Count the number of lists owned by a user.
     *
     * @param owner the user
     * @return count of lists
     */
    long countByOwner(User owner);

    /**
     * Count the number of public lists owned by a user.
     *
     * @param ownerId the user's ID
     * @return count of public lists
     */
    @Query("SELECT COUNT(pl) FROM PlaceList pl WHERE pl.owner.id = :ownerId AND pl.isPublic = true")
    long countPublicListsByOwnerId(@Param("ownerId") Long ownerId);
}
