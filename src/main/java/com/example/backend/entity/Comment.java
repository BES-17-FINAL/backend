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
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comment_post_id", columnList = "post_id"),
        @Index(name = "idx_comment_user_id", columnList = "user_id"),
        @Index(name = "idx_comment_post_created", columnList = "post_id, created_at"),
        @Index(name = "idx_comment_parent_id", columnList = "parent_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    @Builder.Default
    @Column(name = "depth", nullable = false)
    private Integer depth = 0;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

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

    public void setParent(Comment parent) {
        this.parent = parent;
        if (parent != null) {
            this.depth = parent.depth + 1;
        } else {
            this.depth = 0;
        }
    }

    public void addReply(Comment reply) {
        reply.parent = this;
        reply.depth = this.depth + 1;
        this.replies.add(reply);
    }

    public void clearReplies() {
        this.replies.clear();
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isReply() {
        return this.parent != null;
    }

}