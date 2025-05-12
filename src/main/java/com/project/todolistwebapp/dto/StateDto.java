package com.project.todolistwebapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateDto {

    private Long id;

    @NotBlank(message = "The 'name' cannot be empty")
    private String name;

}
