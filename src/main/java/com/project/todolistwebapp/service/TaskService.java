package com.project.todolistwebapp.service;

import com.project.todolistwebapp.config.exception.NullEntityReferenceException;
import com.project.todolistwebapp.dto.TaskDto;
import com.project.todolistwebapp.dto.TaskTransformer;
import com.project.todolistwebapp.model.Task;
import com.project.todolistwebapp.repository.StateRepository;
import com.project.todolistwebapp.repository.TaskRepository;
import com.project.todolistwebapp.repository.ToDoRepository;
import com.project.todolistwebapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ToDoRepository toDoRepository;
    private final StateRepository stateRepository;
    private final TaskTransformer taskTransformer;

    public TaskDto create(TaskDto taskDto){
        Task task =  taskTransformer.fillEntityFields(
                new Task(),
                taskDto,
                toDoRepository.findById(taskDto.getTodoId()).orElseThrow(),
                stateRepository.findByName("New")
        );

        if (task != null) {
            Task savedTask = taskRepository.save(task);
            return taskTransformer.convertToDto(savedTask);
        }
        throw new NullEntityReferenceException("Task cannot be 'null'");
    }

    public Task readById(long id){
        EntityNotFoundException exception = new EntityNotFoundException("Task with id " + id + " not found");
        log.error(exception.getMessage(), exception);

        return taskRepository.findById(id).orElseThrow(
                () -> exception
        );
    }

    public Task update(Task task){
        if (task != null){
            readById(task.getId());
            return taskRepository.save(task);
        }
        throw new NullEntityReferenceException("Task cannot be 'null'");
    }

    public void delete(long id){
        Task task = readById(id);
        taskRepository.delete(task);
    }

    public List<Task> getAll(){
        return taskRepository.findAll();
    }

    public List<Task> getByTodoId(long todoId){
        return taskRepository.getByTodoId(todoId);
    }
}
