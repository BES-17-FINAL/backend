package com.example.backend.controller;

import com.example.backend.dto.PostRequest;
import com.example.backend.dto.PostResponse;
import com.example.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @RequestPart("post") PostRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        PostResponse response = postService.createPost(request, imageFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(

            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable
    ) {
        Page<PostResponse> responsePage = postService.getAllPosts(pageable);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getUserPosts(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable
    ) {
        Page<PostResponse> responsePage = postService.getUserPosts(userId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {

        PostResponse response = postService.getPostById(postId);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestPart("post") PostRequest request,  // 수정
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        PostResponse response = postService.updatePost(postId, request, imageFile);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {

        postService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLikePost(@PathVariable Long postId) {

        postService.toggleLikePost(postId);

        return ResponseEntity.ok().build();
    }
}