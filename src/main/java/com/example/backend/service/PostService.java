package com.example.backend.service;

import com.example.backend.dto.PostImageResponse;
import com.example.backend.dto.PostRequest;
import com.example.backend.dto.PostResponse;
import com.example.backend.dto.PostSearchType;
import com.example.backend.dto.PostSortType;
import com.example.backend.entity.Post;
import com.example.backend.entity.PostCategory;
import com.example.backend.entity.PostImage;
import com.example.backend.entity.PostLike;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.PostLikeRepository;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileUploadService fileUploadService;

    public PostResponse createPost(PostRequest request, MultipartFile[] imageFiles) {
        User currentUser = getCurrentUserFromContext();

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(currentUser)
                .category(request.getCategory())
                .imageUrl(null)
                .build();

        applyImages(post, imageFiles, request.getThumbnailIndex(), true);

        post = postRepository.save(post);
        return mapToPostResponse(post, currentUser);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(
            String keyword,
            PostSearchType searchType,
            PostCategory category,
            PostSortType sortType,
            Pageable pageable
    ) {
        User currentUser = getCurrentUserFromContext();
        List<Post> posts = findPosts(keyword, searchType, category);

        if (posts.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        Map<Long, Long> commentCountMap = posts.stream()
                .collect(Collectors.toMap(
                        Post::getId,
                        post -> commentRepository.countByPostIdAndDeletedAtIsNull(post.getId())
                ));

        Map<Long, Long> likeCountMap = posts.stream()
                .collect(Collectors.toMap(
                        Post::getId,
                        post -> postLikeRepository.countByPostId(post.getId())
                ));

        List<Post> sortedPosts = posts.stream()
                .sorted(buildComparator(sortType, commentCountMap, likeCountMap))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedPosts.size());
        if (start >= sortedPosts.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, sortedPosts.size());
        }

        List<PostResponse> responses = sortedPosts.subList(start, end).stream()
                .map(post -> mapToPostResponse(
                        post,
                        currentUser,
                        commentCountMap.getOrDefault(post.getId(), 0L),
                        likeCountMap.getOrDefault(post.getId(), 0L)
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, sortedPosts.size());
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(Long userId, Pageable pageable) {
        User currentUser = getCurrentUserFromContext();
        Page<Post> posts = postRepository.findByUserUserIdAndDeletedAtIsNull(userId, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUser));
    }

    @Transactional
    public PostResponse getPostById(Long postId) {
        User currentUser = getCurrentUserFromContext();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. (ID: " + postId + ")"));

        post.increaseViewCount();

        return mapToPostResponse(post, currentUser);
    }

    public PostResponse updatePost(Long postId, PostRequest request, MultipartFile[] imageFiles) {
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

        applyImages(post, imageFiles, request.getThumbnailIndex(), false);

        return mapToPostResponse(post, currentUser);
    }

    public void deletePost(Long postId) {
        User currentUser = getCurrentUserFromContext();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. (ID: " + postId + ")"));

        if (!post.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }

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
        Long commentCount = commentRepository.countByPostIdAndDeletedAtIsNull(post.getId());
        Long likeCount = postLikeRepository.countByPostId(post.getId());
        return mapToPostResponse(post, currentUser, commentCount, likeCount);
    }

    private PostResponse mapToPostResponse(Post post, User currentUser, Long commentCount, Long likeCount) {
        boolean isLiked = postLikeRepository.existsByUserAndPost(currentUser, post);

        PostResponse response = PostResponse.fromEntity(post);
        response.setLiked(isLiked);
        response.setCommentCount(commentCount != null ? commentCount : 0L);
        response.setLikeCount(likeCount != null ? likeCount : 0L);

        List<PostImageResponse> imageResponses = post.getImages().stream()
                .sorted(Comparator.comparing(PostImage::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(image -> PostImageResponse.builder()
                        .imageUrl(image.getImageUrl())
                        .thumbnail(image.isThumbnail())
                        .sortOrder(image.getSortOrder())
                        .build())
                .collect(Collectors.toList());
        response.setImages(imageResponses);

        String thumbnailUrl = imageResponses.stream()
                .filter(PostImageResponse::isThumbnail)
                .map(PostImageResponse::getImageUrl)
                .findFirst()
                .orElse(response.getImageUrl());
        response.setThumbnailUrl(thumbnailUrl);

        return response;
    }

    private List<Post> findPosts(String keyword, PostSearchType searchType, PostCategory category) {
        String trimmedKeyword = keyword != null ? keyword.trim() : null;
        boolean hasKeyword = trimmedKeyword != null && !trimmedKeyword.isEmpty();
        Pageable unpaged = Pageable.unpaged();

        if (hasKeyword) {
            PostSearchType effectiveType = searchType != null ? searchType : PostSearchType.TITLE_CONTENT;
            if (category != null) {
                return switch (effectiveType) {
                    case TITLE -> postRepository.findByCategoryAndTitleContainingAndDeletedAtIsNull(category, trimmedKeyword, unpaged).getContent();
                    case CONTENT -> postRepository.findByCategoryAndContentContainingAndDeletedAtIsNull(category, trimmedKeyword, unpaged).getContent();
                    case NICKNAME -> postRepository.findByCategoryAndUserNicknameContainingAndDeletedAtIsNull(category, trimmedKeyword, unpaged).getContent();
                    case TITLE_CONTENT -> postRepository.findByCategoryAndTitleOrContentContainingAndDeletedAtIsNull(category, trimmedKeyword, unpaged).getContent();
                };
            } else {
                return switch (effectiveType) {
                    case TITLE -> postRepository.findByTitleContainingAndDeletedAtIsNull(trimmedKeyword, unpaged).getContent();
                    case CONTENT -> postRepository.findByContentContainingAndDeletedAtIsNull(trimmedKeyword, unpaged).getContent();
                    case NICKNAME -> postRepository.findByUserNicknameContainingAndDeletedAtIsNull(trimmedKeyword, unpaged).getContent();
                    case TITLE_CONTENT -> postRepository.findByTitleOrContentContainingAndDeletedAtIsNull(trimmedKeyword, unpaged).getContent();
                };
            }
        } else {
            if (category != null) {
                return postRepository.findByCategoryAndDeletedAtIsNull(category, unpaged).getContent();
            }
            return postRepository.findAllByDeletedAtIsNull(unpaged).getContent();
        }
    }

    private Comparator<Post> buildComparator(PostSortType sortType, Map<Long, Long> commentCountMap, Map<Long, Long> likeCountMap) {
        PostSortType effectiveSort = sortType != null ? sortType : PostSortType.LATEST;
        return switch (effectiveSort) {
            case OLDEST -> Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case MOST_VIEWS ->
                    Comparator.comparing(Post::getViewCount, Comparator.nullsFirst(Comparator.naturalOrder())).reversed();
            case MOST_COMMENTS ->
                    Comparator.comparing((Post p) -> commentCountMap.getOrDefault(p.getId(), 0L)).reversed()
                            .thenComparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case MOST_LIKES ->
                    Comparator.comparing((Post p) -> likeCountMap.getOrDefault(p.getId(), 0L)).reversed()
                            .thenComparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case LATEST -> Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        };
    }

    private void applyImages(Post post, MultipartFile[] imageFiles, Integer thumbnailIndex, boolean isNewPost) {
        boolean hasNewImages = imageFiles != null && imageFiles.length > 0;

        if (!isNewPost && hasNewImages) {
            List<PostImage> existingImages = new ArrayList<>(post.getImages());
            for (PostImage image : existingImages) {
                fileUploadService.deleteImage(image.getImageUrl());
            }
            post.clearImages();
        }

        if (hasNewImages) {
            int order = 0;
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile == null || imageFile.isEmpty()) {
                    continue;
                }
                String uploadedUrl = fileUploadService.uploadImage(imageFile);
                PostImage image = PostImage.builder()
                        .imageUrl(uploadedUrl)
                        .isThumbnail(false)
                        .sortOrder(order)
                        .build();
                post.addImage(image);
                order++;
            }
        }

        updateThumbnail(post, thumbnailIndex);
    }

    private void updateThumbnail(Post post, Integer thumbnailIndex) {
        if (post.getImages().isEmpty()) {
            return;
        }

        List<PostImage> orderedImages = post.getImages().stream()
                .sorted(Comparator.comparing(PostImage::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        int index = (thumbnailIndex != null) ? thumbnailIndex : 0;
        if (index < 0 || index >= orderedImages.size()) {
            index = 0;
        }

        for (PostImage image : orderedImages) {
            image.setThumbnail(false);
        }
        PostImage thumbnailImage = orderedImages.get(index);
        thumbnailImage.setThumbnail(true);
        post.updateImageUrl(thumbnailImage.getImageUrl());
    }

    private User getCurrentUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        } else {
            throw new ResourceNotFoundException("유저 인증 정보가 올바르지 않습니다. (Principal: " + principal + ")");
        }
    }
}