package com.project.todolistwebapp.dto.userDto;

import com.project.todolistwebapp.model.User;
import com.project.todolistwebapp.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserDtoConverter {

    private final PasswordEncoder passwordEncoder;

    public User convertToEntity(CreateUserDto userDto){
        Objects.requireNonNull(userDto, "User DTO cannot be null");
        return User.createWithEncodedPassword(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()));
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }

    public void fillFields(User user, UpdateUserDto updateUserDto){
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
    }
}
