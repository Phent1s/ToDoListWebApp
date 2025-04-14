package com.project.todolistwebapp.service;

import com.project.todolistwebapp.model.ToDo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToDoSecurityService {

    private final ToDoService toDoService;

    public boolean isOwner(long toDoId, long userId) {
        ToDo toDo = toDoService.readById(toDoId);
        return toDo.getOwner().getId() == userId;
    }

    public boolean isCollaborator(long toDoId, long userId) {
        ToDo toDo = toDoService.readById(toDoId);
        return toDo.getCollaborators()
                .stream().anyMatch(c -> c.getId() == userId);
    }
}
