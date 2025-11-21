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
@Table(name = "post_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(name = "post_bookmark_uk", columnNames = {"user_id", "post_id"})
        },
        indexes = {
                @Index(name = "idx_postbookmark_post_id", columnList = "post_id"),
                @Index(name = "idx_postbookmark_user_id", columnList = "user_id")
        }
)
public class PostBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

}

