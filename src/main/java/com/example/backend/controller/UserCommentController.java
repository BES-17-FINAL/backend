package com.example.backend.controller;

import com.example.backend.dto.CommentResponse;
import com.example.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class UserCommentController {

    private final CommentService commentService;

    // 내가 쓴 댓글 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentResponse>> getUserComments(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {

        Page<CommentResponse> responsePage = commentService.getUserComments(userId, pageable);

        return ResponseEntity.ok(responsePage);
    }
}

