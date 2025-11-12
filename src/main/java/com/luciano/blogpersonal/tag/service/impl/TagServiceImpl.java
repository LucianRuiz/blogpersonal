package com.luciano.blogpersonal.tag.service.impl;

import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.exception.BlogApiException;
import com.luciano.blogpersonal.common.exception.ResourceNotFoundException;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.common.utils.SlugUtils;
import com.luciano.blogpersonal.tag.dto.TagCreateRequest;
import com.luciano.blogpersonal.tag.dto.TagResponse;
import com.luciano.blogpersonal.tag.dto.TagUpdateRequest;
import com.luciano.blogpersonal.tag.mapper.TagMapper;
import com.luciano.blogpersonal.tag.model.Tag;
import com.luciano.blogpersonal.tag.repository.TagRepository;
import com.luciano.blogpersonal.tag.service.TagService;
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
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Autowired
    TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper){
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public TagResponse createTag (TagCreateRequest tagCreateRequest){

        if (tagRepository.existsByName(tagCreateRequest.getName())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.TAG_NAME_EXISTS);
        }
        String slug = SlugUtils.generateUniqueSlug(tagCreateRequest.getName(), tagRepository::existsBySlug);

        Tag tag = tagMapper.toEntity(tagCreateRequest, slug);

        Tag savedTag = tagRepository.save(tag);

        return tagMapper.toDto(savedTag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponse getTagById(Long tagId){
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(()->new ResourceNotFoundException("Tag","Id", tagId));

        return tagMapper.toDto(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponse getTagBySlug(String slug){
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(()-> new ResourceNotFoundException("Tag", "Slug", slug));

        return tagMapper.toDto(tag);
    }

    @Override
    @Transactional
    public TagResponse updateTag(Long tagId, TagUpdateRequest tagUpdateRequest){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.getAuthorities().stream()
                .anyMatch(a ->a.getAuthority().equals("ROLE_ADMIN"))){
            throw new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(()->new ResourceNotFoundException("Tag","Id", tagId));

        if (tagUpdateRequest.getName() != null && !tagUpdateRequest.getName().equals(tag.getName())){
            if (tagRepository.existsByName(tagUpdateRequest.getName())){
                throw  new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.TAG_NAME_EXISTS);
            }

        }

        String newSlug = SlugUtils.generateUniqueSlug(tagUpdateRequest.getName(), tagRepository::existsBySlug);

        tagMapper.updateEntityFromDto(tag, tagUpdateRequest, newSlug);

        Tag savedtag = tagRepository.save(tag);

        return tagMapper.toDto(savedtag);
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentUsername = authentication.getName();

        //
        if (!authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals(AppConstants.ROLE_ADMIN))){
            throw  new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(()->new ResourceNotFoundException("Tag", "Id", tagId));

        // Verificación si el tag tiene posts asociados usando consulta directa
        long postCount = tagRepository.countPostsByTagId(tagId);
        if (postCount > 0){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "No se puede eliminar el tag porque tiene posts asociados");
        }

        tagRepository.delete(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TagResponse> getAllTags(int pageNo, int pageSize, String sortBy, String sortDir){


        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize, sort);

        Page<Tag> tagPage = tagRepository.findAll(pageable);

        List<TagResponse> content = tagPage.getContent().stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());

        return PaginatedResponse.<TagResponse>builder()
                .content(content)
                .pageNo(tagPage.getNumber())
                .pageSize(tagPage.getSize())
                .totalElements(tagPage.getTotalElements())
                .totalPages(tagPage.getTotalPages())
                .first(tagPage.isFirst())
                .last(tagPage.isLast())
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags(){

        return tagRepository.findAll().stream()
               .map(tagMapper::toDto)
               .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getPopularTags(int limit){
        return tagRepository.findAllOrderByPostCountDesc().stream()
                .limit(limit)
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name){
        return tagRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug){
        return tagRepository.existsBySlug(slug);
    }







}
