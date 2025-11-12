package com.luciano.blogpersonal.category.service;

import com.luciano.blogpersonal.category.dto.CategoryCreateRequest;
import com.luciano.blogpersonal.category.dto.CategoryResponse;
import com.luciano.blogpersonal.category.dto.CategoryUpdateRequest;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;

import java.util.List;

public interface CategoryService {

    /**
     * Crea una nueva categoría
     * @param categoryCreateRequest Datos de la categoría a crear
     * @return Respuesta con los datos de la categoría creada
     */
    CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest);

    /**
     * Obtiene una categoría por su ID
     * @param categoryId ID de la categoría a buscar
     * @return Respuesta con los datos de la categoria creada
     */
    CategoryResponse getCategoryById(Long categoryId);

    /**
     * Obtiene una categoría por su slug
     * @param slug Slug de la categoría a buscar
     * @return Respuesta con los datos de la categoría
     */
    CategoryResponse getCategoryBySlug(String slug);

    /**
     * Actualiza una categoría existente
     * @param categoryId ID de la categoría a actualizar
     * @param categoryUpdateRequest Datos para actualizar
     * @return Respuesta con los datos actualizados
     *
     */
    CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest);

    /**
     * Elimina una categoría por su ID
     * @param categoryId ID de la categoría
     */
    void deleteCategory(Long categoryId);

    /**
     * Obtiene todas las categorías paginadas
     * @param pageNo Número de página
     * @param pageSize Tamano de página
     * @param sortBy Campo por el que ordenar
     * @param sortDir Dirección de ordenamiento
     * @return Respuesta paginada con categorías
     */
    PaginatedResponse<CategoryResponse> getAllCategories(int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Obtiene todas las categorías
     * @return Lista de todas las categorías
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Obtiene las categorías más populares por cantidad de posts
     * @param limit Cantidad máxima de categorías a retornar
     * @return Lista de categorías ordenadas por popularidad
     */
    List<CategoryResponse> getPopularCategories(int limit);

    /**
     * Comprueba si existe una categoría con el nombre dado
     * @param name Nombre a comprobar
     * @return true si existe, false en caso contrario
     */
    boolean existByName(String name);

    /**
     * Comprueba si existe una categoría con el slug dado
     * @param slug Slug a comprobar
     * @return true si existe. false en caso contrario
     */
    boolean existBySlug(String slug);

}
