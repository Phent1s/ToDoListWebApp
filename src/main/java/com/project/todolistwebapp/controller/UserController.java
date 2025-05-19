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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new CreateUserDto());
        log.info("User create page loaded");
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") CreateUserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("User create validation error: {}", result.getAllErrors());
            return "create-user";
        }
        try {
            User user = userDtoConverter.convertToEntity(userDto);
            userService.create(user);
            log.info("User created with id {}", user.getId());
            log.info("New user details: {}", user);
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
        log.info("User read page for user with id {}", id);
        return "user-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #id")
    @GetMapping("/{id}/update")
    public String update(@PathVariable Long id, Model model) {
        User user = userService.readById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        log.info("User update page for user with id {}", id);
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
            log.info("Simple user try to change role to admin");
            updateUserDto.setRole(oldUser.getRole());
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            log.warn("User update validation error: {}", result.getAllErrors());
            return "update-user";
        }

        try {
            userService.update(updateUserDto);
            log.info("User updated with id {}", id);
            log.info("Updated user details: {}", updateUserDto);
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
            log.info("User deleted with id {}", id);
            return "redirect:/login?logout";
        }
        userService.delete(id);
        log.info("User deleted with id {}", id);
        return "redirect:/users/all";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        log.info("User list page loaded");
        return "users-list";
    }
}
