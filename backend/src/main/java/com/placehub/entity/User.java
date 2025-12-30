package com.placehub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User Entity - Represents a user in the PlaceHub app.
 */
@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String bio;

    private String avatarUrl;

    private String phone;

    private String url;

    private String city;

    private String country;

    private String address;

    // Optional: User's home place ID
    private String homePlaceId;

    @CreatedDate
    private LocalDateTime createdAt;

    // ========== Relationships (stored as IDs for MongoDB) ==========

    // Places created by this user (stored as IDs)
    @Builder.Default
    private List<String> createdPlaceIds = new ArrayList<>();

    // Place lists owned by this user (stored as IDs)
    @Builder.Default
    private List<String> placeListIds = new ArrayList<>();

    // Users this user is following (stored as IDs)
    @Builder.Default
    private List<String> followingIds = new ArrayList<>();

    // Users following this user (stored as IDs)
    @Builder.Default
    private List<String> followerIds = new ArrayList<>();

    // ========== Helper Methods ==========

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ========== UserDetails Implementation ==========

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // No roles for now
    }

    @Override
    public String getUsername() {
        return email; // Using email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
