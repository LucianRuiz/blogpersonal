package com.luciano.blogpersonal.post.service.impl;

import com.luciano.blogpersonal.category.model.Category;
import com.luciano.blogpersonal.category.repository.CategoryRepository;
import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.exception.BlogApiException;
import com.luciano.blogpersonal.common.exception.ResourceNotFoundException;
import com.luciano.blogpersonal.post.dto.PostCreateRequest;
import com.luciano.blogpersonal.post.dto.PostResponse;
import com.luciano.blogpersonal.post.dto.PostSummaryResponse;
import com.luciano.blogpersonal.post.dto.PostUpdateRequest;
import com.luciano.blogpersonal.post.mapper.PostMapper;
import com.luciano.blogpersonal.post.model.Post;
import com.luciano.blogpersonal.post.repository.PostRepository;
import com.luciano.blogpersonal.post.service.PostService;
import com.luciano.blogpersonal.tag.model.Tag;
import com.luciano.blogpersonal.tag.repository.TagRepository;
import com.luciano.blogpersonal.user.model.User;
import com.luciano.blogpersonal.user.repository.UserRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository, TagRepository tagRepository, PostMapper postMapper){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public PostResponse createPost(PostCreateRequest postCreateRequest){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User author = userRepository.findByUsername(currentUsername)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario", "Username", currentUsername));

        // Crear el post usando el nuevo mapper
        Post post = postMapper.toEntity(postCreateRequest, author);

        // Cargar y asignar categorías
        if (postCreateRequest.getCategoryIds() != null && !postCreateRequest.getCategoryIds().isEmpty()){
            Set<Category> categories = postCreateRequest.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                    .orElseThrow(()->new ResourceNotFoundException("Categoría", "Id", categoryId)))
                .collect(Collectors.toSet());
            post.setCategories(categories);
        }

        // Cargar y asignar tags
        if (postCreateRequest.getTagIds() != null && !postCreateRequest.getTagIds().isEmpty()){
            Set<Tag> tags = postCreateRequest.getTagIds().stream()
                .map(tagId -> tagRepository.findById(tagId)
                    .orElseThrow(()-> new ResourceNotFoundException("Tag", "Id", tagId)))
                .collect(Collectors.toSet());
            post.setTags(tags);
        }

        Post savedPost = postRepository.save(post);

        // Crear response y agregar categorías y tags
        PostResponse response = postMapper.toResponse(savedPost);
        
        // Cargar categorías y tags de manera separada
        Set<Category> categories = categoryRepository.findCategoriesByPostId(savedPost.getId());
        Set<Tag> tags = tagRepository.findTagsByPostId(savedPost.getId());
        
        // Mapear categorías
        response.setCategories(categories.stream()
                .map(category -> PostResponse.CategoryInfo.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        // Mapear tags
        response.setTags(tags.stream()
                .map(tag -> PostResponse.TagInfo.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        return response;

    }

    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest){
        //Obtener el post mediante el id
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post", "Id", postId));

        //Obtenemos el usuario autenticado y extraemos su username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        //Verificamos que el usuario autenticado es el autor o el administrador
        if (!post.getAuthor().getUsername().equals(currentUsername)){
            throw new BlogApiException(HttpStatus.FORBIDDEN, "Usted no tiene los permisos necesarios para realizar esta acción");
        }

        // Actualizar categorías
        if (postUpdateRequest.getCategoryIds()!= null){
            Set<Category> categories = postUpdateRequest.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(()-> new ResourceNotFoundException("Categoría", "Id", categoryId)))
                .collect(Collectors.toSet());
            post.setCategories(categories);
        }

        // Actualizar tags
        if (postUpdateRequest.getTagIds()!= null){
            Set<Tag> tags = postUpdateRequest.getTagIds().stream()
                .map(tagId -> tagRepository.findById(tagId)
                        .orElseThrow(()-> new ResourceNotFoundException("Tag","Id", tagId )))
                .collect(Collectors.toSet());
            post.setTags(tags);
        }

        //Actualizamos el post con los datos del postUpdateRequest
        postMapper.updateEntity(post, postUpdateRequest);

        //Guardamos el post ya con las categorías y tags validadas
        Post savedPost = postRepository.save(post);
        
        // Crear response y agregar categorías y tags
        PostResponse response = postMapper.toResponse(savedPost);
        
        // Cargar categorías y tags de manera separada
        Set<Category> categories = categoryRepository.findCategoriesByPostId(savedPost.getId());
        Set<Tag> tags = tagRepository.findTagsByPostId(savedPost.getId());
        
        // Mapear categorías
        response.setCategories(categories.stream()
                .map(category -> PostResponse.CategoryInfo.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        // Mapear tags
        response.setTags(tags.stream()
                .map(tag -> PostResponse.TagInfo.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post", "Id", postId));
        PostResponse response = postMapper.toResponse(post);
        
        // Cargar categorías y tags de manera separada
        Set<Category> categories = categoryRepository.findCategoriesByPostId(postId);
        Set<Tag> tags = tagRepository.findTagsByPostId(postId);
        
        // Mapear categorías
        response.setCategories(categories.stream()
                .map(category -> PostResponse.CategoryInfo.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        // Mapear tags
        response.setTags(tags.stream()
                .map(tag -> PostResponse.TagInfo.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostBySlug (String slug){
        Post post = postRepository.findBySlug(slug).orElseThrow(()-> new ResourceNotFoundException("Post", "Slug", slug));
        PostResponse response = postMapper.toResponse(post);
        
        // Cargar categorías y tags de manera separada
        Set<Category> categories = categoryRepository.findCategoriesByPostId(post.getId());
        Set<Tag> tags = tagRepository.findTagsByPostId(post.getId());
        
        // Mapear categorías
        response.setCategories(categories.stream()
                .map(category -> PostResponse.CategoryInfo.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        // Mapear tags
        response.setTags(tags.stream()
                .map(tag -> PostResponse.TagInfo.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .collect(Collectors.toSet()));
        
        return response;
    }

    @Override
    @Transactional
    public void deletePostById(Long postId){
        //Obtener el post que se quiere eliminar
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post", "Id", postId));

        //Obtenemos el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Conseguimos su nombre de usuario
        String currentUsername = authentication.getName();

        //Validación si tenemos los permisos necesarios
        if (!post.getAuthor().getUsername().equals(currentUsername) && authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new BlogApiException(HttpStatus.FORBIDDEN, "Usted no tiene los permisos necesarios para realizar esta acción");
        }

        //Eliminar post
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post", "Id", postId));

        post.setViewCount(post.getViewCount()+1);
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PostSummaryResponse> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);

        Page<Post> postPage = postRepository.findAll(pageable);

        return createPostSummaryResponse(postPage);

    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PostSummaryResponse> getAllPublishedPost(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);

        Page<Post> pagePost = postRepository.findByPublishedTrue(pageable);

        return createPostSummaryResponse(pagePost);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PostSummaryResponse> getPostByUserId(Long userId, int pageNo, int pageSize, String sortBy, String sortDir){

        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario", "Id", userId);
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize,sort);

        Page<Post> postPage = postRepository.findByAuthorId(userId, pageable);

        return createPostSummaryResponse(postPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PostSummaryResponse> searchPosts(String keyword, int pageNo, int pageSize){
        //Crear paginación
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        //Los resultados de la busqueda ponerlos en una pagina
        Page<Post> postPage = postRepository.searchPosts(keyword, pageable);
        

        return  createPostSummaryResponse(postPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PostSummaryResponse> getPostsByCategory(Long categoryId, int pageNo, int pageSize){
        if (!categoryRepository.existsById(categoryId)){
            throw new ResourceNotFoundException("Categoría", "Id", categoryId);
        }

        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Post> postPage = postRepository.findByCategoryId(categoryId, pageable);

        return createPostSummaryResponse(postPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<PostSummaryResponse> getPostsByTag(Long tagId, int pageNo, int pageSize){

        if (!tagRepository.existsById(tagId)){
            throw  new ResourceNotFoundException("Tag", "Id", tagId);
        }

        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Post> postPage = postRepository.findByTagId(tagId, pageable);
        return createPostSummaryResponse(postPage);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug){
        return postRepository.existsBySlug(slug);
    }

    //Método auxiliar para convertir Posts en PostSummaryResponse y devolverlos paginados
    private PaginatedResponse<PostSummaryResponse> createPostSummaryResponse(Page<Post> postPage){
        List<Post> posts = postPage.getContent();

        List<PostSummaryResponse> content = posts.stream()
                .map(post -> {
                    PostSummaryResponse response = postMapper.toSummaryResponse(post);
                    
                    // Cargar categorías y tags de manera separada
                    Set<Category> categories = categoryRepository.findCategoriesByPostId(post.getId());
                    Set<Tag> tags = tagRepository.findTagsByPostId(post.getId());
                    
                    // Mapear categorías
                    response.setCategories(categories.stream()
                            .map(category -> PostSummaryResponse.CategoryInfo.builder()
                                    .id(category.getId())
                                    .name(category.getName())
                                    .slug(category.getSlug())
                                    .build())
                            .collect(Collectors.toSet()));
                    
                    // Mapear tags
                    response.setTags(tags.stream()
                            .map(tag -> PostSummaryResponse.TagInfo.builder()
                                    .id(tag.getId())
                                    .name(tag.getName())
                                    .slug(tag.getSlug())
                                    .build())
                            .collect(Collectors.toSet()));
                    
                    return response;
                })
                .collect(Collectors.toList());

        return PaginatedResponse.<PostSummaryResponse>builder()
                .content(content)
                .pageNo(postPage.getNumber())
                .pageSize(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .first(postPage.isFirst())
                .last(postPage.isLast())
                .build();

    }

}