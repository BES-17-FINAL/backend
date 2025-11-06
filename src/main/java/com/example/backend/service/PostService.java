package com.example.backend.service;

import com.example.backend.dto.PostRequest;
import com.example.backend.dto.PostResponse;
import com.example.backend.entity.Post;
import com.example.backend.entity.PostLike;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostLikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileUploadService fileUploadService;

    public PostResponse createPost(PostRequest request, MultipartFile imageFile) {
        User currentUser = getCurrentUserFromContext();

        // 이미지 파일이 있으면 업로드
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileUploadService.uploadImage(imageFile);
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(currentUser)
                .category(request.getCategory())
                .imageUrl(imageUrl)
                .build();

        post = postRepository.save(post);
        return mapToPostResponse(post, currentUser);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        User currentUser = getCurrentUserFromContext();
        Page<Post> posts = postRepository.findAllByDeletedAtIsNull(pageable);
        return posts.map(post -> mapToPostResponse(post, currentUser));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(Long userId, Pageable pageable) {
        User currentUser = getCurrentUserFromContext();
        Page<Post> posts = postRepository.findByUserUserIdAndDeletedAtIsNull(userId, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUser));
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        User currentUser = getCurrentUserFromContext();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. (ID: " + postId + ")"));

        return mapToPostResponse(post, currentUser);
    }

    public PostResponse updatePost(Long postId, PostRequest request, MultipartFile imageFile) {
        User currentUser = getCurrentUserFromContext();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. (ID: " + postId + ")"));

        if (!post.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("이 게시글을 수정할 권한이 없습니다.");
        }

        post.updatePost(
                request.getTitle(),
                request.getContent(),
                request.getCategory()
        );

        // 이미지 파일이 있으면 기존 이미지 삭제 후 새 이미지 업로드
        if (imageFile != null && !imageFile.isEmpty()) {
            // 기존 이미지 삭제
            if (post.getImageUrl() != null) {
                fileUploadService.deleteImage(post.getImageUrl());
            }
            // 새 이미지 업로드
            String newImageUrl = fileUploadService.uploadImage(imageFile);
            post.updateImageUrl(newImageUrl);
        }

        return mapToPostResponse(post, currentUser);
    }

    public void deletePost(Long postId) {
        User currentUser = getCurrentUserFromContext();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. (ID: " + postId + ")"));

        if (!post.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }

        // 이미지 파일도 삭제
        if (post.getImageUrl() != null) {
            fileUploadService.deleteImage(post.getImageUrl());
        }

        post.markAsDeleted();
    }

    @Transactional
    public void toggleLikePost(Long postId) {
        User currentUser = getCurrentUserFromContext();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. (ID: " + postId + ")"));

        Optional<PostLike> like = postLikeRepository.findByUserAndPost(currentUser, post);

        if (like.isPresent()) {
            postLikeRepository.delete(like.get());
        } else {
            PostLike newLike = PostLike.builder()
                    .user(currentUser)
                    .post(post)
                    .build();
            postLikeRepository.save(newLike);
        }
    }

    private PostResponse mapToPostResponse(Post post, User currentUser) {
        boolean isLiked = postLikeRepository.existsByUserAndPost(currentUser, post);

        Long commentCount = commentRepository.countByPostIdAndDeletedAtIsNull(post.getId());

        Long likeCount = postLikeRepository.countByPostId(post.getId());

        PostResponse response = PostResponse.fromEntity(post);

        response.setLiked(isLiked);
        response.setCommentCount(commentCount != null ? commentCount : 0L);
        response.setLikeCount(likeCount != null ? likeCount : 0L);

        return response;
    }

    private User getCurrentUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new ResourceNotFoundException("유저 인증 정보가 올바르지 않습니다. (Principal: " + principal.toString() + ")");
        }
    }
}