package com.example.backend.dto;

import com.example.backend.entity.Comment;
import com.example.backend.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Long parentId;
    private Integer depth;
    private Integer sortOrder;
    private boolean deleted;
    private List<CommentResponse> replies = new ArrayList<>();

    public static CommentResponse fromEntity(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        boolean isDeleted = comment.getDeletedAt() != null;
        response.setDeleted(isDeleted);
        response.setText(isDeleted ? "삭제된 댓글입니다." : comment.getText());
        response.setLikeCount(comment.getLikeCount());
        response.setCreatedAt(comment.getCreatedAt());
        response.setDepth(comment.getDepth());
        response.setSortOrder(comment.getSortOrder());

        if (comment.getParent() != null) {
            response.setParentId(comment.getParent().getId());
        }

        if (comment.getUser() != null) {
            User user = comment.getUser();
            response.setUserId(user.getUserId());
            response.setNickname(user.getNickname());
        }

        return response;
    }
}
