package com.luciano.blogpersonal.category.service.impl;

import com.luciano.blogpersonal.category.dto.CategoryCreateRequest;
import com.luciano.blogpersonal.category.dto.CategoryResponse;
import com.luciano.blogpersonal.category.dto.CategoryUpdateRequest;
import com.luciano.blogpersonal.category.mapper.CategoryMapper;
import com.luciano.blogpersonal.category.model.Category;
import com.luciano.blogpersonal.category.repository.CategoryRepository;
import com.luciano.blogpersonal.category.service.CategoryService;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.exception.BlogApiException;
import com.luciano.blogpersonal.common.exception.ResourceNotFoundException;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.common.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Autowired
    CategoryServiceImpl(CategoryMapper categoryMapper, CategoryRepository categoryRepository){
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest){

        //Autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Verificación de permisos necesarios
        if (!authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals(AppConstants.ROLE_ADMIN))){
            throw  new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Verificación si el nombre ya existe
        if (categoryRepository.existsByName(categoryCreateRequest.getName())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.CATEGORY_NAME_EXISTS);
        }

        //Creación del slug
        String slug = SlugUtils.generateSlug(categoryCreateRequest.getName());

        //Verificación si el slug ya esta asignado
        if (categoryRepository.existsBySlug(slug)){
            throw  new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.CATEGORY_SLUG_EXISTS);
        }

        //Creación de la categoría
        Category Category =categoryMapper.toEntity(categoryCreateRequest, slug);
        //Guardamos la categoría creada
        Category savedCategory =categoryRepository.save(Category);

        //Devolvemos la categoría en forma de respuesta
        return categoryMapper.toDto(savedCategory);

    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId){

        //Buscamos la categoría mediante el id
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Categoría", "Id", categoryId));

        //Retornamos la categoría encontrada en forma de respuesta
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public  CategoryResponse getCategoryBySlug(String slug){
        //Buscamos la categoría mediante el slug
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(()->new ResourceNotFoundException("Categoría", "Slug", slug));
        //Retornamos la categoría encontrada en forma de respuesta
        return categoryMapper.toDto(category);
    }


    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest){

        //Autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Verificación de permisos
        if (!authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals(AppConstants.ROLE_ADMIN))){
            throw new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Encontrar categoría a editar
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Categoría", "Id", categoryId));

        // Variable para almacenar el slug (por defecto el actual)
        String slugToUse = category.getSlug();

        // SOLO si se está actualizando el nombre
        if (categoryUpdateRequest.getName() != null) {
            // Verificar si el nuevo nombre ya está en uso (solo si es diferente al actual)
            if (!categoryUpdateRequest.getName().equals(category.getName()) &&
                    categoryRepository.existsByName(categoryUpdateRequest.getName())){
                throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.CATEGORY_NAME_EXISTS);
            }

            // Generar nuevo slug
            String newSlug = SlugUtils.generateSlug(categoryUpdateRequest.getName());

            // Verificar si el nuevo slug ya está en uso (solo si es diferente al actual)
            if (!category.getSlug().equals(newSlug) && categoryRepository.existsBySlug(newSlug)){
                throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.CATEGORY_SLUG_EXISTS);
            }

            // Usar el nuevo slug
            slugToUse = newSlug;
        }

        //Actualización de la categoría
        categoryMapper.updateEntityFromDto(category, categoryUpdateRequest, slugToUse);

        //Guardando los cambios
        Category savedCategory = categoryRepository.save(category);

        //Retornamos la respuesta
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public  void deleteCategory(Long categoryId){

        //Autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Verificación de permisos
        if (!authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"))){
            throw  new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Encontrar el post a eliminar
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Categoría", "Id", categoryId));

        //Verificación si la categoría tiene post asociados usando consulta directa
        long postCount = categoryRepository.countPostsByCategoryId(categoryId);
        if (postCount > 0){
            throw  new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.CATEGORY_HAS_POSTS);
        }

        //Eliminar categoría
        categoryRepository.delete(category);

    }

    @Override
    @Transactional
    public PaginatedResponse<CategoryResponse> getAllCategories(int pageNo, int pageSize, String sortBy, String sortDir){

        //Creación y elección del ordenamiento
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        //Creación de la paginación
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        //Obtención de datos
        Page<Category> pageCategory = categoryRepository.findAll(pageable);

        //Extracción de datos a una lista
        List<Category> categories = pageCategory.getContent();

        //Conversión de cada categoría a categoryResponse
        List<CategoryResponse> content = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        //Retorno de la respuesta paginada
        return PaginatedResponse.<CategoryResponse> builder()
                .content(content)
                .pageNo(pageCategory.getNumber())
                .pageSize(pageCategory.getSize())
                .totalElements(pageCategory.getTotalElements())
                .totalPages(pageCategory.getTotalPages())
                .first(pageCategory.isFirst())
                .last(pageCategory.isLast())
                .build();
    }

    @Override
    @Transactional
    public List<CategoryResponse> getAllCategories(){

        //Obtención de datos
        List<Category> content = categoryRepository.findAll();

        //Conversión de las categorías a categoryResponse
        List<CategoryResponse> categories = content.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        return categories;
    }

    @Override
    @Transactional
    public List<CategoryResponse> getPopularCategories(int limit){

        List<Category> categories = categoryRepository.findAllOrderByPostCountDesc();
        
        return categories.stream()
                .limit(limit)
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByName(String name){
        //Si existe retorna true
        return categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existBySlug(String slug){
        //Si existe retorna true
        return categoryRepository.existsBySlug(slug);
    }









}