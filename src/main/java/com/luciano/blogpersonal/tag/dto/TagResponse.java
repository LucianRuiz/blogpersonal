package com.luciano.blogpersonal.tag.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TagResponse {
    private Long id;

    private String name;

    private String slug;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int postCount;
}
