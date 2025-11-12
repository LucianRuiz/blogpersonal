package com.luciano.blogpersonal.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryResponse {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String featuredImage;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private int viewCount;
    
    // Información básica del autor
    private Long authorId;
    private String authorName;

    // Información básica de categorías y tags para evitar bucles
    @Builder.Default
    private Set<CategoryInfo> categories = new HashSet<>();

    @Builder.Default
    private Set<TagInfo> tags = new HashSet<>();

    private int commentCount;

    // Clases internas para información básica
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagInfo {
        private Long id;
        private String name;
        private String slug;
    }
}
