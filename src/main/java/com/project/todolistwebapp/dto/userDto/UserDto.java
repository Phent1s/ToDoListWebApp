package com.project.todolistwebapp.dto.userDto;

import com.project.todolistwebapp.model.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;

    @Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.")
    private String firstName;

    @Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.")
    private String lastName;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

}
