package com.example.backend.dto;

import com.example.backend.entity.Comment;
import com.example.backend.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String text;
    private Integer likeCount = 0;
    private LocalDateTime createdAt;
    private Long userId;
    private String nickname;
    private boolean isLiked = false;

    public static CommentResponse fromEntity(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setLikeCount(comment.getLikeCount());
        response.setCreatedAt(comment.getCreatedAt());
        

        if (comment.getUser() != null) {
            User user = comment.getUser();
            response.setUserId(user.getUserId());
            response.setNickname(user.getNickname());
        }

        return response;
    }
}
