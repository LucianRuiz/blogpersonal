package com.luciano.blogpersonal.post.repository;

import com.luciano.blogpersonal.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findBySlug(String slug);
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
    Page<Post> findByPublishedTrue(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "p.published = true AND " +
            "(lower(p.title) LIKE lower(concat('%', :keyword, '%')) OR " +
            "lower(cast(p.content as string)) LIKE lower(concat('%', :keyword, '%')))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.categories c WHERE c.id = :categoryId AND p.published = true")
    Page<Post> findByCategoryId(@Param("categoryId")Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t where t.id = :tagId AND p.published = true")
    Page<Post> findByTagId(@Param("tagId") Long tagId, Pageable pageable);

    boolean existsBySlug(String slug);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}