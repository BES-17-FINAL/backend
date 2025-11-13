package com.example.backend.repository; // 'repository' 폴더 경로입니다.

import com.example.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostIdAndParentIsNull(Long postId, Pageable pageable);

    List<Comment> findByParentIdOrderBySortOrderAsc(Long parentId);

    Long countByPostIdAndParentIsNullAndDeletedAtIsNull(Long postId);

    Long countByParentIdAndDeletedAtIsNull(Long parentId);

    Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId);

    Long countByPostIdAndDeletedAtIsNull(Long postId);
}

