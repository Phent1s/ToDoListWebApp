package com.project.todolistwebapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateDto {

    private Long id;
    private String name;

}
