package com.luciano.blogpersonal.category.controller;

import com.luciano.blogpersonal.category.dto.CategoryCreateRequest;
import com.luciano.blogpersonal.category.dto.CategoryResponse;
import com.luciano.blogpersonal.category.dto.CategoryUpdateRequest;
import com.luciano.blogpersonal.category.service.CategoryService;
import com.luciano.blogpersonal.common.dto.ApiResponse;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejo de categorías
 * Proporciona endpoints para gestión de categorías del blog
 */
@RestController
@RequestMapping("/api/categories")

public class CategoryController {
    private final CategoryService categoryService;

@Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Obtiene todas las categorías (paginadas)
     * GET /api/categories
     */
    @GetMapping()
    public ResponseEntity<PaginatedResponse<CategoryResponse>> getAllCategories(@RequestParam(value = "pageNo",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                                                @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY)String sortBy,
                                                                                @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir){
        PaginatedResponse<CategoryResponse> categories = categoryService.getAllCategories(pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(categories);
    }

    /**
     * Obtiene todas las categorías (sin paginación)
     * GET /api/categories/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories (){
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Obtiene una categoría por su ID
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id){
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Obtiene una categoría por su slug
     * GET /api/categories/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug){
        CategoryResponse category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(category);
    }

    /**
     * Obtiene las categorías más populares
     * GET /api/categories/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<List<CategoryResponse>> getPopularCategories(@RequestParam(value = "limit", defaultValue = "10", required = false) int limit){
        List<CategoryResponse> categories = categoryService.getPopularCategories(limit);
        return ResponseEntity.ok(categories);
    }


    /**
     * Crea una nueva categoría (solo ADMIN)
     * POST /api/categories
     */
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest categoryCreateRequest){
        CategoryResponse createdCategory = categoryService.createCategory(categoryCreateRequest);
        return new ResponseEntity<>(createdCategory,HttpStatus.CREATED);
    }

    /**
     * Actualiza una categoría existente (solo ADMIN)
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory( @PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest){
        CategoryResponse category = categoryService.updateCategory(id, categoryUpdateRequest);
        return ResponseEntity.ok(category);

    }

    /**
     * Elimina una categoría (solo ADMIN)
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Categoría eliminada exitosamente")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si existe una categoría con el nombre dado
     * GET /api/categories/check-name
     */
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse> checkNameExists(@RequestParam String name){
        boolean isExist = categoryService.existByName(name);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message(isExist?"Nombre ya utilizado":"Nombre disponible")
                .build();
        return ResponseEntity.ok(response);
    }


    /**
     * Verifica si existe una categoría con el slug dado
     * GET /api/categories/check-slug
     */
    @GetMapping("/check-slug")
    public ResponseEntity<ApiResponse> checkSlugExists(@RequestParam String slug){
        boolean isExist = categoryService.existBySlug(slug);
        ApiResponse response = ApiResponse.builder()
                .success(isExist)
                .message(isExist?"Slug ya utilizado":"Slug disponible")
                .build();
        return ResponseEntity.ok(response);
    }

}
