package com.luciano.blogpersonal.post.service;

import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.post.dto.PostCreateRequest;
import com.luciano.blogpersonal.post.dto.PostResponse;
import com.luciano.blogpersonal.post.dto.PostSummaryResponse;
import com.luciano.blogpersonal.post.dto.PostUpdateRequest;

public interface PostService {

    /**
     * Crea un nuevo post
     * @param postCreateRequest Datos del post a crear
     * @return Respuesta con los datos del post creado
     */
    PostResponse createPost(PostCreateRequest postCreateRequest);

    /**
     * Actualizar un post existente
     * @param postId ID del post a actualizar
     * @param postUpdateRequest Datos para actualizar
     * @return Respuesta con los datos actualizados
     */
    PostResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest);

    /**
     * Obtiene un post por su id
     * @param postId ID del post a buscar
     * @return Respuesta detallada del post
     */
    PostResponse getPostById (Long postId);

    /**
     * Obtiene un post por su slug
     * @param slug Slug del post a buscar
     * @return Respuesta detallada del post
     */
    PostResponse getPostBySlug (String slug);

    /**
     * Elimina un post por su ID
     * @param postId del post a eliminar
     */
    void deletePostById(Long postId);

    /**
     * Incrementa el contador de vistas de un post
     * @param postId ID del post a incrementar sus vistas
     */
    void incrementViewCount(Long postId);

    /**
     * Obtiene todos los posts paginados
     * @param pageNo Número de página
     * @param pageSize Tamano de la página
     * @param sortBy Campo por el que ordenar
     * @param sortDir Dirección de ordenamiento
     * @return Respuesta paginada con resúmenes de posts
     */
    PaginatedResponse<PostSummaryResponse> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Obtiene todos los posts publicados paginados
     * @param pageNo Número de página
     * @param pageSize Tamano de la página
     * @param sortBy Campo por el que ordenar
     * @param sortDir Dirección del ordenamiento
     * @return Respuesta paginada con resúmenes de posts
     */
    PaginatedResponse<PostSummaryResponse> getAllPublishedPost(int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Obtiene todos los post de un usuario
     * @param userId ID del usuario
     * @param pageNo Número de la página
     * @param pageSize Tamano de la pagina
     * @param sortBy Campo por el que ordenar
     * @param sortDir Dirección del ordenamiento
     * @return Respuesta paginada con resúmenes de posts
     */
    PaginatedResponse<PostSummaryResponse> getPostByUserId(Long userId, int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Busca posts por palabra clave
     * @param keyword Palabra clave a bucsar
     * @param pageNo Número de página
     * @param pageSize Tamano de página
     * @return Respuesta paginada con resúmenes de posts
     */
    PaginatedResponse<PostSummaryResponse> searchPosts(String keyword, int pageNo, int pageSize);

    /**
     * Obtiene posts por categoría
     * @param categoryId ID de la categoría
     * @param pageNo Número de pagina
     * @param pageSize Tamano de la pagina
     * @return Respuesta paginada con resúmenes de posts
     */
    PaginatedResponse<PostSummaryResponse> getPostsByCategory(Long categoryId, int pageNo, int pageSize);

    /**
     * Obtiene posts por etiqueta
     * @param tagId ID de la etiqueta
     * @param pageNo Número de pagina
     * @param pageSize Tamano de la pagina
     * @return Respuesta paginada con resúmenes de posts
     */
    PaginatedResponse<PostSummaryResponse> getPostsByTag(Long tagId, int pageNo, int pageSize);

    /**
     * Comprueba si un post existe por su slug
     * @param slug Slug a comprobar
     * @return true si existe, false en caso contrario
     */

    boolean existsBySlug(String slug);
}
