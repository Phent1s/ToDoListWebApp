package com.project.todolistwebapp.repository;

import com.project.todolistwebapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "select * from tasks where todo_id = ?1", nativeQuery = true)
    List<Task> getByTodoId(Long todoId);

    List<Task> findAllByOrderByIdAsc();

    List<Task> findByTodoIdOrderByIdAsc(Long todoId);
}
