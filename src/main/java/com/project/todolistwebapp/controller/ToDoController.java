package com.project.todolistwebapp.controller;

import com.project.todolistwebapp.config.security.WebAuthenticationToken;
import com.project.todolistwebapp.model.Task;
import com.project.todolistwebapp.model.ToDo;
import com.project.todolistwebapp.model.User;
import com.project.todolistwebapp.service.TaskService;
import com.project.todolistwebapp.service.ToDoService;
import com.project.todolistwebapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService toDoService;
    private final UserService userService;
    private final TaskService taskService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and #ownerId == authentication.principal.id")
    @GetMapping("/create/users/{owner_id}")
    public String createToDoForm(@PathVariable("owner_id") long ownerId, Model model) {
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        return "create-todo";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and #ownerId == authentication.principal.id")
    @PostMapping("/create/users/{owner_id}")
    public String createToDo(@PathVariable("owner_id") long ownerId,
                             @Validated @ModelAttribute("todo") ToDo toDo, BindingResult result) {
        if (result.hasErrors()) {
            return "create-todo";
        }
        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setOwner(userService.readById(ownerId));
        toDoService.create(toDo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and (@toDoSecurityService.isOwner(#id, authentication.principal.id) " +
                  "or @toDoSecurityService.isCollaborator(#id, authentication.principal.id))")
    @GetMapping("/{id}/read")
    public String read(@PathVariable long id, Model model) {
        ToDo toDo = toDoService.readById(id);
        model.addAttribute("todo", toDo);
        List<Task> tasks = taskService.getByTodoId(id);
        List<User> users = userService.getAll().stream()
                .filter(user -> user.getId() != toDo.getOwner().getId())
                .filter(user -> toDo.getCollaborators().stream().allMatch(
                        collaborator -> collaborator.getId() != user.getId()))
                .collect(Collectors.toList());
        model.addAttribute("todo", toDo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);
        return "read-todo";
    }
    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and #ownerId == authentication.principal.id")
    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId, Model model) {
        ToDo toDo = toDoService.readById(todoId);
        model.addAttribute("todo", toDo);
        return "update-todo";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and #ownerId == authentication.principal.id")
    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId,
                         @Validated @ModelAttribute("todo") ToDo todo, BindingResult result, Model model) {
        if (result.hasErrors()) {
            todo.setOwner(userService.readById(ownerId));
            return "update-todo";
        }
        ToDo oldToDo = toDoService.readById(todoId);
        todo.setOwner(oldToDo.getOwner());
        todo.setCollaborators(oldToDo.getCollaborators());
        toDoService.update(todo);
        model.addAttribute(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and #ownerId == authentication.principal.id")
    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) {
        toDoService.delete(todoId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and #userId == authentication.principal.id")
    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") long userId, Model model) {
        List<ToDo> todos = toDoService.getByUserId(userId);
        model.addAttribute("todos", todos);
        model.addAttribute("user", userService.readById(userId));
        return "read-user";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and @toDoSecurityService.isOwner(#id, authentication.principal.id)")
    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        ToDo toDo = toDoService.readById(id);
        List<User> collaborator = toDo.getCollaborators();
        collaborator.add(userService.readById(userId));
        toDo.setCollaborators(collaborator);
        toDoService.update(toDo);
        return "redirect:/todos/" + id + "/read";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and @toDoSecurityService.isOwner(#id, authentication.principal.id)")
    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        ToDo todo = toDoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.remove(userService.readById(userId));
        todo.setCollaborators(collaborators);
        toDoService.update(todo);
        return "redirect:/todos/" + id + "/read";
    }

    // Auxiliary method to be used in authorization rules
    public boolean canReadToDo(long todoId){
        WebAuthenticationToken authenticationToken
                = (WebAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = authenticationToken.getUser();
        ToDo toDo = toDoService.readById(todoId);
        boolean isCollaborator = toDo.getCollaborators().stream().anyMatch(collaborator -> collaborator.getId() == user.getId());
        return user.getId() == toDo.getOwner().getId() || isCollaborator;
    }
}
