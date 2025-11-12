package com.luciano.blogpersonal.comment.dto;

import com.luciano.blogpersonal.post.dto.PostResponse;
import com.luciano.blogpersonal.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean approved;
    private Long postId;
    private UserResponse user;
    private Long parentId;

    @Builder.Default
    private List<CommentResponse> replies = new ArrayList<>();

}
