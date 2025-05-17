package com.project.todolistwebapp.controller;

import com.project.todolistwebapp.dto.userDto.CreateUserDto;
import com.project.todolistwebapp.dto.userDto.UpdateUserDto;
import com.project.todolistwebapp.dto.userDto.UserDto;
import com.project.todolistwebapp.dto.userDto.UserDtoConverter;
import com.project.todolistwebapp.model.User;
import com.project.todolistwebapp.model.UserRole;
import com.project.todolistwebapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new CreateUserDto());
        return "create-user";
    }

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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("{id}/read")
    public String read(@PathVariable Long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #id")
    @GetMapping("/{id}/update")
    public String update(@PathVariable Long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        return "update-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #id")
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

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #id")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(id)) {
            userService.delete(id);
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            return "redirect:/login?logout";
        }
        userService.delete(id);
        return "redirect:/users/all";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }
}
