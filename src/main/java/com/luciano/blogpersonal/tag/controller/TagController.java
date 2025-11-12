package com.luciano.blogpersonal.tag.controller;

import com.luciano.blogpersonal.common.dto.ApiResponse;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.tag.dto.TagCreateRequest;
import com.luciano.blogpersonal.tag.dto.TagResponse;
import com.luciano.blogpersonal.tag.dto.TagUpdateRequest;
import com.luciano.blogpersonal.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejo de tags
 * Proporciona endpoints para gestión de etiquetas del blog
 */

@RestController
@RequestMapping("/api/tags")

public class TagController {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Obtiene todos los tags (paginados)
     * GET /api/tags
     */
    @GetMapping()
    public ResponseEntity<PaginatedResponse<TagResponse>> getAllTags(@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                     @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                                     @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
                                                                     @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir){
        PaginatedResponse<TagResponse> tags = tagService.getAllTags(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(tags);

    }

    /**
     * Obtiene todos los tags (sin paginación)
     * GET /api/tags/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<TagResponse>> getAllTags(){
        List<TagResponse> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    /**
     * Obtiene un tag por su ID
     * GET /api/tags/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById (@PathVariable Long id){
        TagResponse tag = tagService.getTagById(id);
       return ResponseEntity.ok(tag);
    }

    /**
     * Obtiene un tag por su slug
     * GET /api/tags/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<TagResponse> getTagBySlug (@PathVariable String slug){
        TagResponse tag = tagService.getTagBySlug(slug);
        return ResponseEntity.ok(tag);
    }

    /**
     * Obtiene los tags más populares
     * GET /api/tags/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<List<TagResponse>> getPopularTags (@RequestParam (value = "limit", defaultValue = "10", required = false)int limit){
        List<TagResponse> tags = tagService.getPopularTags(limit);
        return ResponseEntity.ok(tags);
    }

    /**
     * Crea un nuevo tag
     * POST /api/tags
     */
    @PostMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TagResponse> createTag (@Valid @RequestBody TagCreateRequest tagCreateRequest){
        TagResponse tag = tagService.createTag(tagCreateRequest);

        return new ResponseEntity<>(tag, HttpStatus.CREATED);
    }

    /**
     * Actualiza un tag existente (solo ADMIN)
     * PUT /api/tags/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponse> updateTag(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest tagUpdateRequest){
        TagResponse tag = tagService.updateTag(id, tagUpdateRequest);
        return ResponseEntity.ok(tag);
    }

    /**
     * Elimina un tag (solo ADMIN)
     * DELETE /api/tags/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteTag(@PathVariable Long id){
        tagService.deleteTag(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Tag eliminado exitosamente")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si existe un tag con el nombre dado
     * GET /api/tags/check-name
     */
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse> checkNameExists(@RequestParam String name){
        boolean isExist = tagService.existsByName(name);
        ApiResponse response = ApiResponse.builder()
                .success(isExist)
                .message(isExist?"Nombre ya usado":"Nombre disponible")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si existe un tag con el slug dado
     * GET /api/tags/check-slug
     */
    @GetMapping("/check-slug")
    public ResponseEntity<ApiResponse> checkSlugExists(@RequestParam String slug){
        boolean isExist = tagService.existsBySlug(slug);
        ApiResponse response = ApiResponse.builder()
                .success(isExist)
                .message(isExist?"Slug ya utilizado":"Slug disponible")
                .build();
        return ResponseEntity.ok(response);
    }
}
