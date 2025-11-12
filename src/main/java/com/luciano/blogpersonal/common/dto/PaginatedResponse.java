package com.luciano.blogpersonal.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PaginatedResponse<T> {
    private List<T> content; //Contenido
    private int pageNo; //Número de paginas
    private int pageSize;//Tamano de pagina
    private long totalElements; //Elementos totales
    private int totalPages; //Total de paginas
    private boolean first; //Primera página
    private boolean last; //Ultima pagina
}
