package com.luciano.blogpersonal.tag.mapper;

import com.luciano.blogpersonal.category.repository.CategoryRepository;
import com.luciano.blogpersonal.common.utils.SlugUtils;
import com.luciano.blogpersonal.tag.dto.TagCreateRequest;
import com.luciano.blogpersonal.tag.dto.TagResponse;
import com.luciano.blogpersonal.tag.dto.TagUpdateRequest;
import com.luciano.blogpersonal.tag.model.Tag;

import com.luciano.blogpersonal.tag.repository.TagRepository;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    private final TagRepository tagRepository;

    public TagMapper(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    public Tag toEntity(TagCreateRequest tagCreateRequest, String slug){

        if (tagCreateRequest == null){
            return null;
        }

        Tag tag = new Tag();
        tag.setName(tagCreateRequest.getName());

        tag.setSlug(slug);

        return tag;
    }

    public void updateEntityFromDto(Tag tag, TagUpdateRequest tagUpdateRequest, String slug){
        if (tagUpdateRequest == null){
            return;
        }

        if (tagUpdateRequest.getName()!= null){
            tag.setName(tagUpdateRequest.getName());

            //Actualización del slug si cambia el nombre
            tag.setSlug(slug);
        }
    }

    public TagResponse toDto(Tag tag){
        if (tag == null){
            return null;
        }


        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .postCount((int)tagRepository.countPostsByTagId(tag.getId()))
                .build();
    }

}
