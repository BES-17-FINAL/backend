package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "comment_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "comment_like_uk",
                        columnNames = {"user_id", "comment_id"}
                )
        },
        indexes = {
                @Index(name = "idx_commentlike_comment_id", columnList = "comment_id"),
                @Index(name = "idx_commentlike_user_id", columnList = "user_id")
        }
)
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
}