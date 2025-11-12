package com.luciano.blogpersonal.comment.service.impl;

import com.luciano.blogpersonal.comment.dto.CommentCreateRequest;
import com.luciano.blogpersonal.comment.dto.CommentResponse;
import com.luciano.blogpersonal.comment.dto.CommentUpdateRequest;
import com.luciano.blogpersonal.comment.mapper.CommentMapper;
import com.luciano.blogpersonal.comment.model.Comment;
import com.luciano.blogpersonal.comment.repository.CommentRepository;
import com.luciano.blogpersonal.comment.service.CommentService;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.exception.BlogApiException;
import com.luciano.blogpersonal.common.exception.ResourceNotFoundException;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.post.model.Post;
import com.luciano.blogpersonal.post.repository.PostRepository;
import com.luciano.blogpersonal.user.model.User;
import com.luciano.blogpersonal.user.repository.UserRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Autowired
    CommentServiceImpl (UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, CommentMapper commentMapper){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional
    public CommentResponse createComment(CommentCreateRequest commentCreateRequest) {
        //Obtenemos el username del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        //Obtenemos el usuario autenticado con el username
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(()->new ResourceNotFoundException("Usuario", "username", currentUsername));

        //Obtenemos el id del post
        Long postId = commentCreateRequest.getPostId();
        //Obtenemos el post con el id
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post","Id", postId));

        //Creamos la variable para almacenar el parentComment.
        Comment parentComment = null;
        //Verificamos si el comentario tiene padre
        if(commentCreateRequest.getParentId() != null){
            Long parentId = commentCreateRequest.getParentId();
            parentComment = commentRepository.findById(parentId)
                    .orElseThrow(()->new ResourceNotFoundException("Comentario", "Id", parentId));

            //Verificamos que el comentario padre esté enlazado al mismo post del comentario
            if (!parentComment.getPost().getId().equals(post.getId())){
                throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.INVALID_PARENT_COMMENT);
            }
        }

        //Creamos el comentario
        Comment createdComment = commentMapper.toEntity(commentCreateRequest, user, post, parentComment);
        //Guardamos el comentario
        commentRepository.save(createdComment);
        //Enlistamos la lista de comentarios del post
        List<Comment> postComments = post.getComments().stream().collect(Collectors.toList());

        return commentMapper.toDto(createdComment, postComments);
    }

    /**
     * Crea una respuesta a un comentario existente.
     *
     * Este método está diseñado para trabajar con un endpoint REST específico donde el ID del comentario
     * padre se obtiene de la URL (como PathVariable) y no del cuerpo de la solicitud.
     * Por ejemplo: POST /api/comments/{parentId}/replies
     *
     * El método automáticamente obtiene el postId del comentario padre, por lo que el cliente
     * solo necesita proporcionar el contenido del comentario en la solicitud.
     *
     * @param parentId ID del comentario padre, obtenido de la URL del endpoint
     * @param commentCreateRequest Datos de la respuesta a crear (solo se requiere el contenido)
     * @return Respuesta con los datos del comentario creado
     */
    @Override
    @Transactional
    public CommentResponse createReply (Long parentId, CommentCreateRequest commentCreateRequest){

        //Obtenemos el comentario padre para en base a este obtener el postId
        Comment commentParent = commentRepository.findById(parentId)
                        .orElseThrow(()->new ResourceNotFoundException("Comentario", "Id", parentId));

        commentCreateRequest.setPostId(commentParent.getPost().getId());

        commentCreateRequest.setParentId(parentId);

        return createComment(commentCreateRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId){
        //Obtención del comentario
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFoundException("Comentario", "Id", commentId));

        //Creamos una lista en la cual almacenaremos los comentarios del post para tener la jerarquía del comentario
        List<Comment> postComments = comment.getPost().getComments().stream().collect(Collectors.toList());
        //Devolvemos la respuesta
        return commentMapper.toDto(comment, postComments);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest commentUpdateRequest){
        //Autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        //Obtención del comentario
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFoundException("Comentario", "Id", commentId));

        //Verificación de permisos
        if (!comment.getUser().getUsername().equals(currentUsername)
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(AppConstants.ROLE_ADMIN))){
            throw new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Actualización de datos
        commentMapper.updateEntityFromDto(comment, commentUpdateRequest);
        Comment updatedComment = commentRepository.save(comment);

        //Retorno de respuesta al cliente
        List<Comment> postComments = comment.getPost().getComments().stream().collect(Collectors.toList());
        return commentMapper.toDto(updatedComment, postComments);

    }

    @Override
    @Transactional
    public void deleteComment(Long commentId){
        //Autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        //Obtención del comentario a borrar
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFoundException("Comentario", "Id", commentId));

        //Verificación de permisos
        if (!comment.getUser().getUsername().equals(currentUsername) && !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Eliminación del comentario
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public List<CommentResponse> getCommentsByPostId(Long postId){

        //Obtención del post
       Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("Post", "Id", postId));

        //Obtención de los comentarios de un post
        List<Comment> comments = post.getComments().stream().collect(Collectors.toList());

        //Retorno de la funcion toDtoList que nos permite ordenar todos los comentarios de un post de manera jerárquica
        return commentMapper.toDtoList(comments);

    }

    @Override
    @Transactional
    public PaginatedResponse<CommentResponse> getCommentsByPostIdPaginated(Long postId, int pageNo, int pageSize){

        //Creamos la paginación
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        //Creamos la pagina de la cual obtendremos el contenido y datos necesarios para devolver la respuesta paginada
        Page<Comment> commentPage = commentRepository.findByPostIdAndParentIsNull(postId, pageable);

        //Obtenemos el postId para obtener todas las respuestas, necesarias para el toDto del mapper por la jerarquía
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("Post", "Id", postId));

        //Obtención de todas las respuestas del post
        List<Comment> allComments = post.getComments().stream().collect(Collectors.toList());

        //Convertimos a CommentResponse los comentarios seleccionados en commentPage
        List<CommentResponse> content = commentPage.getContent().stream()
                .map(comment -> commentMapper.toDto(comment,allComments))
                .collect(Collectors.toList());

        //Devolvemos la respuesta con los datos del commentPage paginado.
        return PaginatedResponse.<CommentResponse>builder()
                .content(content)
                .pageNo(commentPage.getNumber())
                .pageSize(commentPage.getSize())
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .first(commentPage.isFirst())
                .last(commentPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public PaginatedResponse<CommentResponse> getRepliesByCommentIdPaginated(Long commentId, int pageNo, int pageSize){

        //Creamos la paginación
        Pageable pageable = PageRequest.of(pageNo, pageSize,Sort.by("createdAt").descending());

        //Obtenemos el comentario padre o elegido mediante el id para obtener el post al que pertenece y a la vez verificamos que el id del comentario exista
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(()-> new ResourceNotFoundException("Comment", "Id", commentId));

        //Obtenemos los comentarios que son hijos del comentario elegido
        Page<Comment> commentPage = commentRepository.findByParentId(commentId, pageable);


        //Obtenemos todos los comentarios del post donde se encuentra el comentario elegido
        List<Comment> allComments = parentComment.getPost().getComments().stream().collect(Collectors.toList());

        //Convertimos a DTO cada respuesta del comentario elegido
        List<CommentResponse> content = commentPage.getContent().stream()
                .map(comment -> commentMapper.toDto(comment, allComments))
                .collect(Collectors.toList());

        //Retornamos la respuesta paginada con las respuestas del comentario elegido
        return PaginatedResponse.<CommentResponse>builder()
                .content(content)
                .pageNo(commentPage.getNumber())
                .pageSize(commentPage.getSize())
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .first(commentPage.isFirst())
                .last(commentPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public PaginatedResponse<CommentResponse> getCommentsByUserId(Long userId, int pageNo, int pageSize){
        //Verificamos que el Id del usuario exista
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario", "Id", userId));

        //Creamos la paginación
        Pageable pageable = PageRequest.of(pageNo,pageSize,Sort.by("createdAt").descending());

        //Obtenemos la pagina con la información a mostrar
        Page <Comment> commentPage = commentRepository.findByUserId(userId, pageable);

        //Convertimos cada comentario de la pagina en un DTO y lo almacenamos en la misma lista
        List<CommentResponse> content = commentPage.getContent().stream()
                .map(comment -> commentMapper.toDto(comment, comment.getPost().getComments().stream().collect(Collectors.toList())))
                .collect(Collectors.toList());

        //Devolvemos la respuesta con los datos del commentPage paginado.
        return PaginatedResponse .<CommentResponse>builder()
                .content(content)
                .pageNo(commentPage.getNumber())
                .pageSize(commentPage.getSize())
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .first(commentPage.isFirst())
                .last(commentPage.isLast())
                .build();


    }


    @Override
    @Transactional
    public CommentResponse approveComment(Long commentId){
        //Autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        //Verificamos que el usuario autenticado sea admin
        if (!authentication.getAuthorities().stream()
                .anyMatch(a-> a.getAuthority().equals("ROLE_ADMIN"))){
            throw  new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Obtenemos el comentario mediante su id
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFoundException("Comentario", "Id", commentId));

        //Modificamos la aprobación del comentario
        comment.setApproved(true);

        //Actualizamos el comentario con el nuevo parametro
        Comment updatedComment = commentRepository.save(comment);

        //Obtenemos la lista de comentarios del post ya que es necesario para obtener la estructura de jerarquia del comentario
        List<Comment> postComments = comment.getPost().getComments().stream().collect(Collectors.toList());

        //Retornamos el comentario actualizado en DTO
        return commentMapper.toDto(updatedComment, postComments);
    }

    @Override
    @Transactional
    public CommentResponse disapproveComment(Long commentId) {

        //Autenticación

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Verificamos que el usuario autenticado sea admin

        if (!authentication.getAuthorities().stream()
                .anyMatch(a-> a.getAuthority().equals("ROLE_ADMIN"))){
            throw  new BlogApiException(HttpStatus.FORBIDDEN, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Obtenemos el comentario mediante su id

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new ResourceNotFoundException("Comentario", "Id", commentId));

        //Modificamos la aprobación del comentario

        comment.setApproved(false);

        //Actualizamos el comentario con el nuevo parametro

        Comment updatedComment = commentRepository.save(comment);

        //Obtenemos la lista de comentarios del post ya que es necesario para obtener la estructura de jerarquia del comentario

        List<Comment> postComments = comment.getPost().getComments().stream().collect(Collectors.toList());

        //Retornamos el comentario actualizado en DTO

        return commentMapper.toDto(updatedComment, postComments);
    }










}
