package com.luciano.blogpersonal.category.repository;

import com.luciano.blogpersonal.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Category c ORDER BY (SELECT COUNT(pc) FROM Post p JOIN p.categories pc WHERE pc.id = c.id) DESC")
    List<Category> findAllOrderByPostCountDesc();

    @Query("SELECT COUNT(p) FROM Post p JOIN p.categories c WHERE c.id = :categoryId")
    long countPostsByCategoryId( @Param("categoryId") Long categoryId);

    @Query("SELECT c FROM Category c JOIN Post p ON c MEMBER OF p.categories WHERE p.id = :postId")
    Set<Category> findCategoriesByPostId(@Param("postId") Long postId);
}
