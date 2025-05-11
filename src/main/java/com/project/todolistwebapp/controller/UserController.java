package com.project.todolistwebapp.controller;

import com.project.todolistwebapp.dto.userDto.CreateUserDto;
import com.project.todolistwebapp.dto.userDto.UpdateUserDto;
import com.project.todolistwebapp.dto.userDto.UserDto;
import com.project.todolistwebapp.dto.userDto.UserDtoConverter;
import com.project.todolistwebapp.model.User;
import com.project.todolistwebapp.model.UserRole;
import com.project.todolistwebapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new CreateUserDto());
        return "create-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") CreateUserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return "create-user";
        }
        try {
            User user = userDtoConverter.convertToEntity(userDto);
            userService.create(user);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("password", "error.user", "Password encoding error");
            return "create-user";
        }
    }

    @PreAuthorize("hasRole('ADMIN') " +
                  "or #id == authentication.principal.id")
    @GetMapping("{id}/read")
    public String read(@PathVariable Long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @PreAuthorize("hasRole('ADMIN') " +
                  "or #id == authentication.principal.id")
    @GetMapping("/{id}/update")
    public String update(@PathVariable Long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        return "update-user";
    }

    @PreAuthorize("hasRole('ADMIN') " +
                  "or #id == authentication.principal.id")
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, Model model,
                         @Validated @ModelAttribute("user") UpdateUserDto updateUserDto, BindingResult result) {
        UserDto oldUser = userService.findByIdThrowing(id);
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != UserRole.ADMIN) {
            updateUserDto.setRole(oldUser.getRole());
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }

        try {
            userService.update(updateUserDto);
            return "redirect:/users/" + id + "/read";
        }catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }
    }

    @PreAuthorize("hasRole('ADMIN') " +
                  "or #id == authentication.principal.id")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(id)) {
            userService.delete(id);
            SecurityContextHolder.clearContext();
            return "redirect:/logout";
        }
        userService.delete(id);
        return "redirect:/users/all";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }
}
