package com.project.todolistwebapp.service;

import com.project.todolistwebapp.config.exception.NullEntityReferenceException;
import com.project.todolistwebapp.dto.StateDto;
import com.project.todolistwebapp.model.State;
import com.project.todolistwebapp.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;

    public State create(State state) {
        if (state != null){
            return stateRepository.save(state);
        }
        throw new NullEntityReferenceException("State cannot be null");
    }

    public State readById(Long id) {
        return stateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("State with id " + id + " not found")
        );
    }

    public State update(State state) {
        if (state != null){
            readById(state.getId());
            return stateRepository.save(state);
        }
        throw new NullEntityReferenceException("State cannot be null");
    }

    public void delete(Long id) {
        State state = readById(id);
        stateRepository.delete(state);
    }

    public List<State> getAll() {
        return stateRepository.findAllByOrderByIdAsc();
    }

    public State getByName(String name) {
        Optional<State> optional = Optional.ofNullable(stateRepository.findByName(name));
        if (optional.isPresent()){
            return optional.get();
        }
        throw new EntityNotFoundException("State with name " + name + " not found");
    }

    public List<State> findAll() {
        return stateRepository.findAllByOrderByIdAsc()
                .stream()
                .toList();
    }

    public StateDto toDto(State state) {
        return StateDto.builder()
                .name(state.getName())
                .build();
    }
}
