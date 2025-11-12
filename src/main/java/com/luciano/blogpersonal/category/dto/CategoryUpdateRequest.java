package com.luciano.blogpersonal.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CategoryUpdateRequest {
    @Size(min = 2, max = 50, message = "El nombre de la categoría debe tener entre 2 y 50 caracteres")
    private String name;
    @Size(max = 200, message = "La descripción de la categoría debe tener como máximo 255 caracteres")
    private String description;
}
