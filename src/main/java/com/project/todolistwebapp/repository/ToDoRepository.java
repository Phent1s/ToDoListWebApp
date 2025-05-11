package com.project.todolistwebapp.repository;

import com.project.todolistwebapp.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {

//    @Query(value = "select id, title, created_at, owner_id from todos where owner_id = ?1 union " +
//                   "select id, title, created_at, owner_id from todos inner join todo_collaborator on id = todo_id and " +
//                   "collaborator_id = ?1", nativeQuery = true)
//    List<To Do> getByUserId(Long userId);

    @Query(value = """
    SELECT t.id, t.title, t.created_at, t.owner_id 
    FROM todos t
    WHERE t.owner_id = :userId
    
    UNION
    
    SELECT t.id, t.title, t.created_at, t.owner_id 
    FROM todos t
    INNER JOIN todo_collaborator tc ON t.id = tc.todo_id 
    WHERE tc.collaborator_id = :userId
    
    ORDER BY created_at ASC
    """,
            nativeQuery = true)
    List<ToDo> findByUserWithCollaborationSorted(Long userId);

}
