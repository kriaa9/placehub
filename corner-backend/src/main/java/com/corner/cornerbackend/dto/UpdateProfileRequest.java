package com.corner.cornerbackend.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 40, message = "First name cannot exceed 40 characters")
    private String firstName;

    @Size(max = 40, message = "Last name cannot exceed 40 characters")
    private String lastName;

    @Size(max = 300, message = "Bio cannot exceed 300 characters")
    private String bio;

    @URL(message = "Avatar URL must be a valid URL")
    private String avatarUrl;

    private String phone;
}
