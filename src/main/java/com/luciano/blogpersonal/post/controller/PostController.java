package com.luciano.blogpersonal.post.controller;

import com.luciano.blogpersonal.common.dto.ApiResponse;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.post.dto.PostCreateRequest;
import com.luciano.blogpersonal.post.dto.PostResponse;
import com.luciano.blogpersonal.post.dto.PostSummaryResponse;
import com.luciano.blogpersonal.post.dto.PostUpdateRequest;
import com.luciano.blogpersonal.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejo de posts
 * Proporciona endpoints para gestión de publicaciones del blog
 */

@RestController
@RequestMapping("/api/posts")

public class PostController {
    private final PostService postService;


    @Autowired
    public PostController (PostService postService){
        this.postService = postService;
    }

    /**
     * Obtiene todos los post publicados ("público")
     * GET /api/posts
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<PostSummaryResponse>> getPostsPublished(@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                    @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                                                    @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
                                                                                    @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        PaginatedResponse<PostSummaryResponse> posts= postService.getAllPublishedPost(pageNo,pageSize, sortBy, sortDir);
        return ResponseEntity.ok(posts);


    }

    /**
     * Obtiene todos los posts (incluye borradores - solo para admin)
     * GET /api/posts/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<PostSummaryResponse>> getAllPosts(@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                              @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                                              @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
                                                                              @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir){

        PaginatedResponse<PostSummaryResponse> posts = postService.getAllPosts(pageNo,pageSize,sortBy,sortDir);
        return ResponseEntity.ok(posts);
    }

    /**
     * Obtiene un post por su ID
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById (@PathVariable Long id){
        PostResponse response = postService.getPostById(id);
        postService.incrementViewCount(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un post por su slug
     * GET /api/posts/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<PostResponse> getPostBySlug(@PathVariable String slug){
        PostResponse post = postService.getPostBySlug(slug);
        postService.incrementViewCount(post.getId());
        return ResponseEntity.ok(post);
    }

    /**
     * Crea un nuevo post
     * POST /api/posts
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest postCreateRequest){
        PostResponse post = postService.createPost(postCreateRequest);
        return new ResponseEntity<>(post,HttpStatus.CREATED);
    }

    /**
     * Actualiza un post existente
     * PUT /api/posts/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PostResponse> updatePost (@PathVariable Long id,@Valid @RequestBody PostUpdateRequest postUpdateRequest){
        PostResponse post = postService.updatePost(id, postUpdateRequest);
        return ResponseEntity.ok(post);
    }

    /**
     * Elimina un post
     * DELETE /api/posts/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long id){
        postService.deletePostById(id);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Post eliminado exitosamente")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene posts de un usuario específico
     * GET /api/posts/user/{userId}
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<PaginatedResponse<PostSummaryResponse>> getPostByUserId(@PathVariable Long id,
                                                                                  @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                  @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                                                  @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
                                                                                  @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir){
        PaginatedResponse<PostSummaryResponse> posts = postService.getPostByUserId(id,pageNo,pageSize,sortBy,sortDir);
        return ResponseEntity.ok(posts);
    }

    /**
     * Busca posts por palabra clave
     * GET /api/posts/search
     */
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<PostSummaryResponse>> searchPosts (@RequestParam String keyword,
                                                                               @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                               @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize){
        PaginatedResponse<PostSummaryResponse>posts = postService.searchPosts(keyword, pageNo, pageSize);
        return ResponseEntity.ok(posts);
    }

    /**
     * Obtiene posts por categoría
     * GET /api/posts/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PaginatedResponse<PostSummaryResponse>> getPostsByCategory (@PathVariable Long categoryId,
                                                                  @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                  @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize){
        PaginatedResponse<PostSummaryResponse>posts = postService.getPostsByCategory(categoryId, pageNo, pageSize);
        return ResponseEntity.ok(posts);
    }

    /**
     * Obtiene posts por tag
     * GET /api/posts/tag/{tagId}
     */
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<PaginatedResponse<PostSummaryResponse>> getPostsByTag (@PathVariable Long tagId,
                                                                                 @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                 @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize){
        PaginatedResponse<PostSummaryResponse>posts = postService.getPostsByTag(tagId, pageNo, pageSize);
        return ResponseEntity.ok(posts);
    }

    /**
     * Verifica si existe un post con el slug dado
     * GET /api/posts/check-slug
     */
    @GetMapping("/check-slug")
    public ResponseEntity<ApiResponse> existBySlug (@RequestParam String slug){
        boolean isExist = postService.existsBySlug(slug);
        ApiResponse response = ApiResponse.builder()
                .success(isExist)
                .message(isExist?"Slug ya esta en uso" :"Slug disponible")
                .build();
        return ResponseEntity.ok(response);
    }

}
