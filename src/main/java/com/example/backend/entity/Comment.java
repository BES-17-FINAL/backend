package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "comments", indexes = {

        @Index(name = "idx_comment_post_id", columnList = "post_id"),
        @Index(name = "idx_comment_user_id", columnList = "user_id"),
        @Index(name = "idx_comment_post_created", columnList = "post_id, created_at"),
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public Comment(Post post, User user, String text) {
        this.post = post;
        this.user = user;
        this.text = text;
        this.likeCount = 0;
    }

    public void updateText(String newText) {
        this.text = newText;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void addLike() {
        this.likeCount += 1;
    }

    public void removeLike() {
        if (this.likeCount > 0) {
            this.likeCount -= 1;
        }
    }

}