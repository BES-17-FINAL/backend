package com.example.backend.repository;

import com.example.backend.entity.Post;
import com.example.backend.entity.PostLike;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    boolean existsByUserAndPost(User user, Post post);

    Optional<PostLike> findByUserAndPost(User user, Post post);

    @Modifying
    void deleteByUserAndPost(User user, Post post);
}

