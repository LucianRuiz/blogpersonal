package com.luciano.blogpersonal.comment.controller;

import com.luciano.blogpersonal.comment.dto.CommentCreateRequest;
import com.luciano.blogpersonal.comment.dto.CommentResponse;
import com.luciano.blogpersonal.comment.dto.CommentUpdateRequest;
import com.luciano.blogpersonal.comment.service.CommentService;
import com.luciano.blogpersonal.common.dto.ApiResponse;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejo de comentarios
 * Proporciona endpoints para gestión de comentarios en posts
 */
@RestController
@RequestMapping("/api")

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Obtiene todos los comentarios de un post (estructura jerárquica)
     * GET /api/posts/{postId}/comments
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentByPostId(@PathVariable Long postId){
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Obtiene comentarios de nivel superior de un post (paginados)
     * GET /api/posts/{postId}/comments/paginated
     */
    @GetMapping("/posts/{postId}/comments/paginated")
    public ResponseEntity<PaginatedResponse<CommentResponse>> getCommentByPostIdPaginated(@PathVariable Long postId,
                                                                                          @RequestParam(value = "pageNo",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                          @RequestParam(value = "pageSize",defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize){
        PaginatedResponse<CommentResponse> comments = commentService.getCommentsByPostIdPaginated(postId, pageNo, pageSize);
        return ResponseEntity.ok(comments);
    }

    /**
     * Crea un nuevo comentario en un post
     * POST /api/posts/{postId}/comments
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest commentCreateRequest){
        CommentResponse comment = commentService.createComment(commentCreateRequest);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    /**
     * Crea una respuesta a un comentario existente
     * POST /api/comments/{commentId}/replies
     */
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentResponse> createReply(@PathVariable Long commentId, @Valid @RequestBody CommentCreateRequest commentCreateRequest){
        CommentResponse comment = commentService.createReply(commentId, commentCreateRequest);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);

    }

    /**
     * Obtiene un comentario por su ID
     * GET /api/comments/{id}
     */
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id){
       CommentResponse comment =  commentService.getCommentById(id);
       return ResponseEntity.ok(comment);

    }

    /**
     * Obtiene las respuestas de un comentario (paginadas)
     * GET /api/comments/{commentId}/replies
     */
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<PaginatedResponse<CommentResponse>> getRepliesByCommentIdPaginated (@PathVariable Long commentId,
                                                                                              @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                              @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize){
       PaginatedResponse<CommentResponse> comments = commentService.getRepliesByCommentIdPaginated(commentId,pageNo,pageSize);
       return ResponseEntity.ok(comments);
    }

    /**
     * Actualiza un comentario existente
     * PUT /api/comments/{id}
     */
    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment (@PathVariable Long id, @Valid @RequestBody CommentUpdateRequest commentUpdateRequest){
        CommentResponse comment = commentService.updateComment(id,commentUpdateRequest);
        return ResponseEntity.ok(comment);
    }

    /**
     * Elimina un comentario
     * DELETE /api/comments/{id}
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse> deleteComment (@PathVariable Long id){
        commentService.deleteComment(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("El comentario fue eliminado exitosamente")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los comentarios de un usuario (paginados)
     * GET /api/users/{userId}/comments
     */
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<PaginatedResponse<CommentResponse>> getCommentByUserId (@PathVariable Long userId,
                                                                                  @RequestParam(value = "pageNo",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                  @RequestParam(value = "pageSize",defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)int pageSize){
        PaginatedResponse<CommentResponse> comments = commentService.getCommentsByUserId(userId,pageNo,pageSize);
        return ResponseEntity.ok(comments);
    }

    /**
     * Aprueba un comentario (solo ADMIN)
     * PUT /api/comments/{id}/approve
     */
    @PutMapping("/comments/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> approveComment(@PathVariable Long id){
        CommentResponse comment = commentService.approveComment(id);

        return ResponseEntity.ok(comment);
    }

    /**
     * Desaprueba un comentario (solo ADMIN)
     * PUT /api/comments/{id}/disapprove
     */
    @PutMapping("/comments/{id}/disapprove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> disapproveComment(@PathVariable Long id){
        CommentResponse comment = commentService.disapproveComment(id);

        return ResponseEntity.ok(comment);
    }
}
