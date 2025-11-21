package com.example.backend.repository;

import com.example.backend.entity.Post;
import com.example.backend.entity.PostBookmark;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

    @Query("SELECT COUNT(pb) FROM PostBookmark pb WHERE pb.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    boolean existsByUserAndPost(User user, Post post);

    Optional<PostBookmark> findByUserAndPost(User user, Post post);

}

