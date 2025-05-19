package com.project.todolistwebapp.controller;

import com.project.todolistwebapp.dto.TaskDto;
import com.project.todolistwebapp.dto.TaskTransformer;
import com.project.todolistwebapp.model.Task;
import com.project.todolistwebapp.model.TaskPriority;
import com.project.todolistwebapp.service.StateService;
import com.project.todolistwebapp.service.TaskService;
import com.project.todolistwebapp.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;
    private final TaskTransformer taskTransformer;

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or (@toDoSecurityService.isOwner(#todoId, @userService.getCurrentUser().id) " +
                  "or @toDoSecurityService.isCollaborator(#todoId, @userService.getCurrentUser().id))")
    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") Long todoId, Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", toDoService.readById(todoId));
        model.addAttribute("priorities", TaskPriority.values());
        log.info("Create task page loaded");
        return "create-task";
    }

    @PreAuthorize("hasAuthority('ADMIN')" +
                  "or (@toDoSecurityService.isOwner(#todoId, @userService.getCurrentUser().id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, @userService.getCurrentUser().id)")
    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") Long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result){
        if (result.hasErrors()) {
            model.addAttribute("todo", toDoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            log.warn("Create task validation errors: {}", result.getAllErrors());
            return "create-task";
        }

        taskService.create(taskDto);
        log.info("Task with id = {} was created successfully", taskDto.getId());
        log.info("New task details: {}", taskDto);
        return "redirect:/todos/" + todoId + "/read";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or (@toDoSecurityService.isOwner(#todoId, @userService.getCurrentUser().id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, @userService.getCurrentUser().id)")
    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String taskUpdateForm(@PathVariable("task_id") Long taskId, @PathVariable("todo_id") Long todoId, Model model) {
        TaskDto taskDto = taskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("states", stateService.getAll());
        log.info("Task update form were loaded");
        return "update-task";
    }

    @PreAuthorize("hasAuthority('ADMIN')" +
                  "or (@toDoSecurityService.isOwner(#todoId, @userService.getCurrentUser().id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, @userService.getCurrentUser().id)")
    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("todo_id") Long todoId, @PathVariable("task_id") Long taskId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("states", stateService.getAll());
            log.warn("Update task validation errors: {}", result.getAllErrors());
            return "update-task";
        }
        Task task = taskTransformer.fillEntityFields(
                new Task(),
                taskDto,
                toDoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId())
        );
        taskService.update(task);
        log.info("Task with id = {} was updated successfully", taskId);
        log.info("Updated task details: {}", taskDto);
        return "redirect:/todos/" + todoId + "/read";
    }

    @PreAuthorize("hasAuthority('ADMIN')" +
                  "or (@toDoSecurityService.isOwner(#todoId, @userService.getCurrentUser().id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, @userService.getCurrentUser().id)")
    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") Long taskId, @PathVariable("todo_id") Long todoId){
        taskService.delete(taskId);
        log.info("Task with id = {} was deleted successfully", taskId);
        return "redirect:/todos/" + todoId + "/read";
    }
}
