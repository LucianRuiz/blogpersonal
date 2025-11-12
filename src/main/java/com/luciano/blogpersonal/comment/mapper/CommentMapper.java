package com.luciano.blogpersonal.comment.mapper;

import com.luciano.blogpersonal.comment.dto.CommentCreateRequest;
import com.luciano.blogpersonal.comment.dto.CommentResponse;
import com.luciano.blogpersonal.comment.dto.CommentUpdateRequest;
import com.luciano.blogpersonal.comment.model.Comment;
import com.luciano.blogpersonal.post.model.Post;
import com.luciano.blogpersonal.user.mapper.UserMapper;
import com.luciano.blogpersonal.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    private final UserMapper userMapper;

    @Autowired
    public CommentMapper(UserMapper userMapper){
        this.userMapper = userMapper;

    }

    public Comment toEntity (CommentCreateRequest commentCreateRequest, User user, Post post, Comment parent){
        if (commentCreateRequest == null){
            return null;
        }

        Comment comment = new Comment();
        comment.setContent(commentCreateRequest.getContent());
        comment.setUser(user);
        comment.setPost(post);
        comment.setParent(parent);

        return comment;

    }

    public void updateEntityFromDto (Comment comment, CommentUpdateRequest commentUpdateRequest){

        if (commentUpdateRequest == null){
            return;
        }

        if (commentUpdateRequest.getContent() != null ){
            comment.setContent(commentUpdateRequest.getContent());
        }
    }

    //Metodo para convertir un comentario y sus hijos a commentResponse
    public CommentResponse toDto (Comment comment, List<Comment> allReplies){
        if (comment == null){
            return null;
        }

        CommentResponse commentResponse = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .approved(comment.isApproved())
                .postId(comment.getPost().getId())
                .user(userMapper.toDto(comment.getUser()))
                .parentId(comment.getParent() != null ? comment.getParent().getId():null)
                .replies(new ArrayList<>())
                .build();


        //Primero filtramos
        List<Comment> directReplies = allReplies.stream()
                .filter(reply -> reply.getParent() != null && reply.getParent().getId().equals(comment.getId()))
                .collect(Collectors.toList());

        //Ahora llenamos la lista de respuestas del comentario

        for (Comment reply : directReplies){
            commentResponse.getReplies().add(toDto(reply, allReplies));
        }

        return commentResponse;


    }

    //Metodo para convertir todos los comentarios padre y sus hijos a commentResponse.
    // Util para mostrar todos los comentarios en un post
    public List<CommentResponse> toDtoList (List<Comment> comments){
        if (comments == null){
            return new ArrayList<>();
        }
        //Aplicamos el filtraje sobre la lista de todos los comentarios de un post para obtener solo los comentarios padres (topLevelComments)
        List<Comment> topLevelComments = comments.stream()
                .filter(comment -> comment.getParent() == null)
                .collect(Collectors.toList());

        //Ahora llenamos la lista de comentarios de cada comentario padre
        return topLevelComments.stream()
                .map(comment -> toDto(comment, comments))
                .collect(Collectors.toList());
    }
}
