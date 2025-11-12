package com.luciano.blogpersonal.comment.service;

import com.luciano.blogpersonal.comment.dto.CommentCreateRequest;
import com.luciano.blogpersonal.comment.dto.CommentResponse;
import com.luciano.blogpersonal.comment.dto.CommentUpdateRequest;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;

import java.util.List;

public interface CommentService {

    /**
     * Crea un nuevo comentario en un post
     * @param commentCreateRequest Datos del comentario a crear
     * @return Respuesta con los datos del comentario creado
     *
     */
    CommentResponse createComment(CommentCreateRequest commentCreateRequest);

    /**
     * Crea una respuesta a un comentario existente
     * @param parentId ID del comentario padre
     * @param commentCreateRequest Daos de la respuesta a crear
     * @return Respuesta con los datos del comentario creado
     */
    CommentResponse createReply(Long parentId, CommentCreateRequest commentCreateRequest);

    /**
     * Obtiene un comentario por su ID
     * @param commentId ID del comentario a buscar
     * @return Respuesta con los datos del comentario
     */
    CommentResponse getCommentById(Long commentId);

    /**
     * Actualiza un comentario existente
     * @param commentId ID del comentario a actualizar
     * @param commentUpdateRequest Daos para actualizar
     * @return Respuesta con los datos actualizados
     */
    CommentResponse updateComment(Long commentId, CommentUpdateRequest commentUpdateRequest);

    /**
     * Elimina un comentario por su ID
     * @param commentId ID del comentario a eliminar
     */
    void deleteComment(Long commentId);

    /**
     * Obtiene todos los comentarios de un post
     * @param postId ID del post
     * @return Lista de comentarios con sus respuestas anidadas
     */
    List<CommentResponse> getCommentsByPostId(Long postId);

    /**
     * Obtiene los comentarios de nivel superior de un post paginados
     * @param postId ID del post
     * @param pageNo Numero de página
     * @param pageSize Tamano de la pagina
     * @return Respuesta paginada con comentarios
     */
    PaginatedResponse<CommentResponse> getCommentsByPostIdPaginated(Long postId, int pageNo, int pageSize);

    /**
     * Obtiene las respuestas de un comentario paginadas
     * @param commentId ID del comentario padre
     * @param pageNo Número de página
     * @param pageSize Tamano de página
     * @return Respuesta paginada con comentarios
     */
    PaginatedResponse<CommentResponse> getRepliesByCommentIdPaginated(Long commentId, int pageNo, int pageSize);

    /**
     * Obtiene todos los comentarios de un usuario paginados
     * @param userId ID del usuario
     * @param pageNo Número de página
     * @param pageSize Tamano de página
     * @return Respuesta paginada con comentarios
     */
    PaginatedResponse<CommentResponse> getCommentsByUserId(Long userId, int pageNo, int pageSize);

    /**
     * Aprueba un comentario (para moderación)
     * @param commentId ID del comentario a aprobar
     * @return Respuesta con los d tos del comentario
     */
    CommentResponse approveComment(Long commentId);

    /**
     * Desaprueba un comentario (para moderación)
     * @param commentId ID del comentario a desaprobar
     * @return Respuesta con los datos del comentario
     */
    CommentResponse disapproveComment(Long commentId);
}
