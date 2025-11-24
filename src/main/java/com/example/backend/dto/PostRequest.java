package com.example.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.backend.entity.PostCategory;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostRequest {

    private String title;

    private String content;

    private PostCategory category;

    //썸네일 인덱스
    private Integer thumbnailIndex;
    
    // 삭제할 기존 이미지 URL 목록
    private List<String> deletedImageUrls;
    
    // 이미지 순서 정보
    private List<String> imageOrderUrls;
}
