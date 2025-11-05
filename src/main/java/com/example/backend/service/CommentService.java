package com.example.backend.service;
import com.example.backend.dto.CommentRequest;
import com.example.backend.dto.CommentResponse;
import com.example.backend.entity.Comment;
import com.example.backend.entity.CommentLike;
import com.example.backend.entity.Post;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.repository.CommentLikeRepository;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {


    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentResponse createComment(Long postId, CommentRequest request) {

        User currentUser = getCurrentUserFromContext();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. (ID: " + postId + ")"));

        Comment comment = Comment.builder()
                .text(request.getText())
                .post(post)
                .user(currentUser)
                .build();

        comment = commentRepository.save(comment);
        return CommentResponse.fromEntity(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {

        User currentUser = getCurrentUserFromContext();

        if (!postRepository.existsByIdAndDeletedAtIsNull(postId)) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. (ID: " + postId + ")");
        }

        Page<Comment> comments = commentRepository.findByPostIdAndDeletedAtIsNull(postId, pageable);

        return comments.map(comment -> {
            boolean isLiked = commentLikeRepository.existsByUserAndComment(currentUser, comment);

            CommentResponse response = CommentResponse.fromEntity(comment);

            response.setLiked(isLiked);

            return response;
        });
    }

    public CommentResponse updateComment(Long commentId, CommentRequest request) {

        User currentUser = getCurrentUserFromContext();

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다. (ID: " + commentId + ")"));

        if (!comment.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("댓글을 수정할 권한이 없습니다.");
        }

        comment.updateText(request.getText());

        return CommentResponse.fromEntity(comment);
    }

    public void deleteComment(Long commentId) {

        User currentUser = getCurrentUserFromContext();

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다. (ID: " + commentId + ")"));

        if (!comment.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.markAsDeleted();

    }

    public void toggleLikeComment(Long commentId) {
        User currentUser = getCurrentUserFromContext();

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다. (ID: " + commentId + ")"));

        Optional<CommentLike> like = commentLikeRepository.findByUserAndComment(currentUser, comment);

        if (like.isPresent()) {
            commentLikeRepository.delete(like.get());
            comment.removeLike();

        } else {
            CommentLike newLike = CommentLike.builder()
                    .user(currentUser)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(newLike);

            comment.addLike();
        }
    }

    private User getCurrentUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("로그인이 필요하거나, 유효한 토큰이 아닙니다.");
        }

        Object principal = authentication.getPrincipal();

        // 3. 'JwtFilter'가 'User' 객체를 넣었는지 확인합니다.
        if (principal instanceof User) {
            // 4. 'User' 객체로 '형변환'해서 '바로' 반환(return)합니다!
            return (User) principal;
        } else {
            throw new ResourceNotFoundException("유저 인증 정보가 올바르지 않습니다. (Principal: " + principal.toString() + ")");
        }
    }
}

