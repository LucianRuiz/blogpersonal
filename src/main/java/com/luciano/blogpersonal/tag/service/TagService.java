package com.luciano.blogpersonal.tag.service;

import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.tag.dto.TagCreateRequest;
import com.luciano.blogpersonal.tag.dto.TagResponse;
import com.luciano.blogpersonal.tag.dto.TagUpdateRequest;

import java.util.List;

public interface TagService {

    /**
     * Crea una nueva etiqueta
     * @param tagCreateRequest Datos de la etiqueta a crear
     * @return Respuesta con los datos de la etiqueta a crear
     */
    TagResponse createTag (TagCreateRequest tagCreateRequest);

    /**
     * Obtiene una etiqueta por su ID
     * @param tagId ID de la etiqueta a buscar
     * @return Respuesta con los datos de la etiqueta
     */
    TagResponse getTagById(Long tagId);

    /**
     * Obtiene una etiqueta por su slug
     * @param slug Slug de la etiqueta a buscar
     * @return Respuesta con los datos de la etiqueta
     */
    TagResponse getTagBySlug(String slug);

    /**
     * Actualiza una etiqueta existente
     * @param tagId ID de la etiqueta a actualizar
     * @param tagUpdateRequest Datos para actualizar
     * @return Respuesta con los datos actualizados
     */
    TagResponse updateTag(Long tagId, TagUpdateRequest tagUpdateRequest);

    /**
     * Elimina una etiqueta por su ID
     * @param tagId ID de la etiqueta a eliminar
     */
    void deleteTag(Long tagId);

    /**
     * Obtiene todas las etiquetas paginadas
     * @param pageNo Número de página
     * @param pageSize Tamaño de página
     * @param sortBy Campo por el que ordenar
     * @param sortDir Dirección de ordenamiento
     * @return Respuesta paginada con etiquetas
     */
    PaginatedResponse<TagResponse> getAllTags(int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Obtiene todas las etiquetas
     * @return Lista de todas las etiquetas
     */
    List<TagResponse> getAllTags();

    /**
     * Obtiene las etiquetas más populares por cantidad de posts
     * @param limit Cantidad máxima de etiquetas a retornar
     * @return Lista de etiquetas ordenadas por popularidad
     */
    List<TagResponse> getPopularTags(int limit);

    /**
     * Comprueba si existe una etiqueta con el nombre dado
     * @param name Nombre a comprobar
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Comprueba si existe una etiqueta con el slug dado
     * @param slug Slug a comprobar
     * @return true si existe, false en caso contrario
     */
    boolean existsBySlug(String slug);
}

