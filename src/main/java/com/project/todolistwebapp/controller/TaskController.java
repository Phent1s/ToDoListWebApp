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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and (@toDoSecurityService.isOwner(#todoId, authentication.principal.id) " +
                  "or @toDoSecurityService.isCollaborator(#todoId, authentication.principal.id))")
    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", toDoService.readById(todoId));
        model.addAttribute("priorities", TaskPriority.values());
        return "create-task";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')" +
                  "and (@toDoSecurityService.isOwner(#todoId, authentication.principal.id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, authentication.principal.id)")
    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result){
        if (result.hasErrors()) {
            model.addAttribute("todo", toDoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            return "create-task";
        }

        taskService.create(taskDto);
        log.info("Task with id = {} was created successfully", taskDto.getId());

        return "redirect:/todos/" + todoId + "/read";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') " +
                  "and (@toDoSecurityService.isOwner(#todoId, authentication.principal.id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, authentication.principal.id)")
    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String taskUpdateForm(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        TaskDto taskDto = taskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("states", stateService.getAll());
        return "update-task";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')" +
                  "and (@toDoSecurityService.isOwner(#todoId, authentication.principal.id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, authentication.principal.id)")
    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("task_id") long taskId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("states", stateService.getAll());
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
        return "redirect:/todos/" + todoId + "/read";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')" +
                  "and (@toDoSecurityService.isOwner(#todoId, authentication.principal.id))" +
                  "or @toDoSecurityService.isCollaborator(#todoId, authentication.principal.id)")
    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId){
        taskService.delete(taskId);
        log.info("Task with id = {} was deleted successfully", taskId);
        return "redirect:/todos/" + todoId + "/read";
    }
}
