package com.project.todolistwebapp.service;

import com.project.todolistwebapp.config.exception.NullEntityReferenceException;
import com.project.todolistwebapp.config.security.WebAuthenticationToken;
import com.project.todolistwebapp.dto.userDto.UpdateUserDto;
import com.project.todolistwebapp.dto.userDto.UserDto;
import com.project.todolistwebapp.dto.userDto.UserDtoConverter;
import com.project.todolistwebapp.model.User;
import com.project.todolistwebapp.model.UserRole;
import com.project.todolistwebapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    public  User create(User role){
        if (role != null) {
            return userRepository.save(role);
        }
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    public User readById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public UserDto update(UpdateUserDto updateUserDto){
        User user = userRepository.findById(updateUserDto.getId()).orElseThrow(EntityNotFoundException::new);
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(user.getId()) && currentUser.getRole() == UserRole.ADMIN && updateUserDto.getRole() != user.getRole()) {
            throw new IllegalStateException("Admin cannot change his own role!");
        }

        if (currentUser.getRole() == UserRole.ADMIN && !currentUser.getId().equals(user.getId())){
            user.setRole(updateUserDto.getRole());
        } else if (updateUserDto.getRole() != user.getRole()) {
            throw new IllegalStateException("Only admins can change roles of other users");
        }
        userDtoConverter.fillFields(user, updateUserDto);
        userRepository.save(user);
        return userDtoConverter.toDto(user);
    }

    public void delete(Long id){
        User user = readById(id);
        userRepository.delete(user);
    }

    public List<User> getAll(){
        return userRepository.findAllByOrderByIdAsc();
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByEmail(username);
    }

    public User getCurrentUser(){
        WebAuthenticationToken authentication
                = (WebAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getDetails();
    }

    public Optional<UserDto> findById(Long id){
        return userRepository.findById(id).map(userDtoConverter::toDto);
    }

    public UserDto findByIdThrowing(Long id){
        return userRepository.findById(id).map(userDtoConverter::toDto).orElseThrow(EntityNotFoundException::new);
    }

    public List<UserDto> findAll(){
        return userRepository.findAllByOrderByIdAsc().stream().map(userDtoConverter::toDto).toList();
    }
}
