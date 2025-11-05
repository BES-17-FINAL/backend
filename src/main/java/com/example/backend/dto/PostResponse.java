package com.example.backend.dto;

import com.example.backend.entity.Post;
import com.example.backend.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private Long userId;
    private String nickname;
    private Long likeCount = 0L;
    private boolean isLiked = false;
    private Long commentCount = 0L;

    public static PostResponse fromEntity(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt());

        if (post.getUser() != null) {
            User user = post.getUser();
            response.setUserId(user.getUserId());
            response.setNickname(user.getNickname());
        }

        return response;
    }
}
