package com.project.todolistwebapp.service;

import com.project.todolistwebapp.model.ToDo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToDoSecurityService {

    private final ToDoService toDoService;

    public boolean isOwner(Long toDoId, Long userId) {
        ToDo toDo = toDoService.readById(toDoId);
        return toDo.getOwner().getId().equals(userId);
    }

    public boolean isCollaborator(Long toDoId, Long userId) {
        ToDo toDo = toDoService.readById(toDoId);
        return toDo.getCollaborators()
                .stream().anyMatch(c -> c.getId().equals(userId));
    }
}
