package com.example.backend.repository;

import com.example.backend.entity.Post;
import com.example.backend.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);
    boolean existsByIdAndDeletedAtIsNull(Long postId);
    Page<Post> findByUserUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);
    long countByUserUserIdAndDeletedAtIsNull(Long userId);

    Page<Post> findByCategoryAndDeletedAtIsNull(PostCategory category, Pageable pageable);

    Page<Post> findByUserNicknameContainingAndDeletedAtIsNull(String nickname, Pageable pageable);
    Page<Post> findByTitleContainingAndDeletedAtIsNull(String title, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByTitleOrContentContainingAndDeletedAtIsNull(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.category = :category AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByCategoryAndTitleOrContentContainingAndDeletedAtIsNull(
            @Param("category") PostCategory category,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    Page<Post> findByCategoryAndTitleContainingAndDeletedAtIsNull(
            PostCategory category,
            String title,
            Pageable pageable
    );
    Page<Post> findByCategoryAndUserNicknameContainingAndDeletedAtIsNull(
            PostCategory category,
            String nickname,
            Pageable pageable
    );
}

