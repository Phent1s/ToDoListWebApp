package com.project.todolistwebapp.repository;

import com.project.todolistwebapp.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    State findByName(String name);

    List<State> findAllByOrderByIdAsc();
}
