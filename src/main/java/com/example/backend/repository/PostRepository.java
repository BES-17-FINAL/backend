package com.example.backend.repository;

import com.example.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);

    boolean existsByIdAndDeletedAtIsNull(Long postId);

    Page<Post> findByUserUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    long countByUserUserIdAndDeletedAtIsNull(Long userId);

}

