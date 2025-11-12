package com.luciano.blogpersonal.post.mapper;

import com.luciano.blogpersonal.post.dto.PostCreateRequest;
import com.luciano.blogpersonal.post.dto.PostResponse;
import com.luciano.blogpersonal.post.dto.PostSummaryResponse;
import com.luciano.blogpersonal.post.dto.PostUpdateRequest;
import com.luciano.blogpersonal.post.model.Post;
import com.luciano.blogpersonal.user.model.User;
import com.luciano.blogpersonal.common.utils.SlugUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostMapper {

    public Post toEntity(PostCreateRequest request, User author) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setSlug(SlugUtils.generateSlug(request.getTitle()));
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setFeaturedImage(request.getFeaturedImage());
        post.setPublished(request.isPublished());
        post.setAuthor(author);
        
        // Si se publica, establecer la fecha de publicación
        if (request.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }
        
        return post;
    }

    public Post updateEntity(Post post, PostUpdateRequest request) {
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
            post.setSlug(SlugUtils.generateSlug(request.getTitle()));
        }
        if (request.getExcerpt() != null) {
            post.setExcerpt(request.getExcerpt());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getFeaturedImage() != null) {
            post.setFeaturedImage(request.getFeaturedImage());
        }
        
        // Manejar publicación
        if (!post.isPublished() && request.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }
        post.setPublished(request.isPublished());
        
        return post;
    }

    public PostResponse toResponse(Post post) {
        PostResponse response = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .content(post.getContent())
                .featuredImage(post.getFeaturedImage())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .published(post.isPublished())
                .viewCount(post.getViewCount())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .build();
        
        // Información del autor
        if (post.getAuthor() != null) {
            response.setAuthorId(post.getAuthor().getId());
            response.setAuthorName(post.getAuthor().getUsername());
        }
        
        return response;
    }

    public PostSummaryResponse toSummaryResponse(Post post) {
        PostSummaryResponse response = PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .featuredImage(post.getFeaturedImage())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .build();
        
        // Información del autor
        if (post.getAuthor() != null) {
            response.setAuthorId(post.getAuthor().getId());
            response.setAuthorName(post.getAuthor().getUsername());
        }
        
        return response;
    }
}
