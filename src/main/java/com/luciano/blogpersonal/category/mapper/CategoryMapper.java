package com.luciano.blogpersonal.category.mapper;

import com.luciano.blogpersonal.category.dto.CategoryCreateRequest;
import com.luciano.blogpersonal.category.dto.CategoryResponse;
import com.luciano.blogpersonal.category.dto.CategoryUpdateRequest;
import com.luciano.blogpersonal.category.model.Category;
import com.luciano.blogpersonal.category.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

  private final CategoryRepository categoryRepository;

    public CategoryMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public Category toEntity(CategoryCreateRequest categoryCreateRequest, String slug){
        if (categoryCreateRequest == null){
            return null;
        }

        Category category = new Category();
        category.setName(categoryCreateRequest.getName());
        category.setSlug(slug);
        category.setDescription(categoryCreateRequest.getDescription());


        return category;
    }

    public void updateEntityFromDto(Category category, CategoryUpdateRequest categoryUpdateRequest, String slug){
        if (categoryUpdateRequest == null){
            return ;
        }

        if (categoryUpdateRequest.getName()!=null){
            category.setName(categoryUpdateRequest.getName());
            category.setSlug(slug);
        }

        if (categoryUpdateRequest.getDescription()!=null){
            category.setDescription(categoryUpdateRequest.getDescription());
        }

    }

    public CategoryResponse toDto (Category category){
        if (category == null){
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .postCount((int)categoryRepository.countPostsByCategoryId(category.getId()))
                .build();
    }


}
