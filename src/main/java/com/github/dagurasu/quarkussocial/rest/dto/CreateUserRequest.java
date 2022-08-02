package com.github.dagurasu.quarkussocial.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class CreateUserRequest {

    @NotBlank(message="Name may not be blank")
    private String name;

    @NotNull(message="Age may not be null")
    private Integer age;
    
}
