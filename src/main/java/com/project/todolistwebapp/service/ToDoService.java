package com.project.todolistwebapp.service;

import com.project.todolistwebapp.config.exception.NullEntityReferenceException;
import com.project.todolistwebapp.model.ToDo;
import com.project.todolistwebapp.repository.ToDoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;

    public ToDoService(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    public ToDo create(ToDo toDo){
        if(toDo != null){
            return toDoRepository.save(toDo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    public ToDo readById(Long id){
        return toDoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ToDo with id " + id + " not found")
        );
    }

    public ToDo update(ToDo toDo){
        if(toDo != null){
            readById(toDo.getId());
            return toDoRepository.save(toDo);
        }
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    public void delete(Long id){
        ToDo todo = readById(id);
        toDoRepository.delete(todo);
    }

    public List<ToDo> gteAll(){ return toDoRepository.findAll(); }

    public List<ToDo> getByUserId(Long userId){return toDoRepository.findByUserWithCollaborationSorted(userId);}
}
