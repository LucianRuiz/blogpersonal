package com.luciano.blogpersonal.tag.repository;

import com.luciano.blogpersonal.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Optional<Tag> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    @Query("SELECT t FROM Tag t ORDER BY (SELECT COUNT(pt) FROM Post p JOIN p.tags pt WHERE pt.id = t.id) DESC")
    List<Tag> findAllOrderByPostCountDesc();

    @Query("SELECT COUNT(p) FROM Post p JOIN p.tags t WHERE t.id = :tagId")
    long countPostsByTagId(@Param("tagId")Long tagId);

    @Query("SELECT t FROM Tag t JOIN Post p ON t MEMBER OF p.tags WHERE p.id = :postId")
    Set<Tag> findTagsByPostId(@Param("postId") Long postId);
}
