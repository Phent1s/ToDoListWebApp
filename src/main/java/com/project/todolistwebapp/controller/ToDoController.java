package com.project.todolistwebapp.controller;

import com.project.todolistwebapp.config.security.WebAuthenticationToken;
import com.project.todolistwebapp.model.Task;
import com.project.todolistwebapp.model.ToDo;
import com.project.todolistwebapp.model.User;
import com.project.todolistwebapp.service.TaskService;
import com.project.todolistwebapp.service.ToDoService;
import com.project.todolistwebapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService toDoService;
    private final UserService userService;
    private final TaskService taskService;

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #ownerId")
    @GetMapping("/create/users/{owner_id}")
    public String createToDoForm(@PathVariable("owner_id") Long ownerId, Model model) {
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        log.info("Create ToDo form loaded");
        return "create-todo";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #ownerId")
    @PostMapping("/create/users/{owner_id}")
    public String createToDo(@PathVariable("owner_id") Long ownerId,
                             @Validated @ModelAttribute("todo") ToDo toDo, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Create ToDo validation error: {}", result.getAllErrors());
            return "create-todo";
        }
        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setOwner(userService.readById(ownerId));
        toDoService.create(toDo);
        Long currentUserId = userService.getCurrentUser().getId();
        log.info("ToDo with id {} were created.", toDo.getId());
        log.info("New ToDo details: {}", toDo);
        return "redirect:/todos/all/users/" + currentUserId;
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or (@toDoSecurityService.isOwner(#id, @userService.getCurrentUser().id) " +
                  "or @toDoSecurityService.isCollaborator(#id, @userService.getCurrentUser().id))")
    @GetMapping("/{id}/read")
    public String read(@PathVariable Long id, Model model) {
        ToDo toDo = toDoService.readById(id);
        model.addAttribute("todo", toDo);
        List<Task> tasks = taskService.getByTodoId(id);
        List<User> users = userService.getAll().stream()
                .filter(user -> !Objects.equals(user.getId(), toDo.getOwner().getId()))
                .filter(user -> toDo.getCollaborators().stream().noneMatch(
                        collaborator -> Objects.equals(collaborator.getId(), user.getId())))
                .collect(Collectors.toList());
        model.addAttribute("todo", toDo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);
        log.info("Read ToDo page loaded");
        return "read-todo";
    }
    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #ownerId")
    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") Long todoId, @PathVariable("owner_id") Long ownerId, Model model) {
        ToDo toDo = toDoService.readById(todoId);
        model.addAttribute("todo", toDo);
        log.info("Update ToDo page loaded");
        return "update-todo";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #ownerId")
    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") Long todoId, @PathVariable("owner_id") Long ownerId,
                         @Validated @ModelAttribute("todo") ToDo todo, BindingResult result, Model model) {
        if (result.hasErrors()) {
            todo.setOwner(userService.readById(ownerId));
            log.warn("Update ToDo validation error: {}", result.getAllErrors());
            return "update-todo";
        }
        ToDo oldToDo = toDoService.readById(todoId);
        todo.setOwner(oldToDo.getOwner());
        todo.setCollaborators(oldToDo.getCollaborators());
        toDoService.update(todo);
        model.addAttribute(todo);
        Long currentUserId = userService.getCurrentUser().getId();
        log.info("ToDo with ID {} was updated.", todo.getId());
        log.info("Updated ToDo details: {}", todo);
        return "redirect:/todos/all/users/" + currentUserId;
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #ownerId")
    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") Long todoId, @PathVariable("owner_id") Long ownerId) {
        toDoService.delete(todoId);
        Long currentUserId = userService.getCurrentUser().getId();
        log.info("ToDo with ID {} was deleted.", todoId);
        return "redirect:/todos/all/users/" + currentUserId;
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getCurrentUser().id == #userId")
    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") Long userId, Model model) {
        List<ToDo> todos = toDoService.getByUserId(userId);
        model.addAttribute("todos", todos);
        model.addAttribute("user", userService.readById(userId));
        log.info("All ToDos page loaded");
        return "read-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @toDoSecurityService.isOwner(#id, @userService.getCurrentUser().id)")
    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable Long id, @RequestParam("user_id") Long userId) {
        ToDo toDo = toDoService.readById(id);
        List<User> collaborator = toDo.getCollaborators();
        collaborator.add(userService.readById(userId));
        toDo.setCollaborators(collaborator);
        toDoService.update(toDo);
        log.info("Collaborator {} was added to ToDo {}.",userId ,id);
        return "redirect:/todos/" + id + "/read";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @toDoSecurityService.isOwner(#id, @userService.getCurrentUser().id)")
    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable Long id, @RequestParam("user_id") Long userId) {
        ToDo todo = toDoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.remove(userService.readById(userId));
        todo.setCollaborators(collaborators);
        toDoService.update(todo);
        log.info("Collaborator {} was deleted from ToDo {}.",userId ,id);
        return "redirect:/todos/" + id + "/read";
    }
}
