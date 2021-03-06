package com.arounders.web.dto;

import com.arounders.web.entity.Attachment;
import com.arounders.web.entity.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long id;
    private String title;
    private String content;
    private String region;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isHidden;
    private String thumbnailName;
    private String thumbnailPath;
    private String status;

    private Long memberId;
    private Long categoryId;
    private Integer cityId;

    private String city;

    /* 작성자 */
    private String writer;
    /* 댓글 개수 */
    private int commentCount;
    /* 좋아요 개수 */
    private int likeCount;
    /* 관심 개수 */
    private int interestCount;
    /* 카테고리 제목 */
    private String categoryTitle;
    /* 이미지 리스트 */
    private List<Attachment> attachments = new ArrayList<>();

    /* 좋아요 여부 */
    private Long isLike;
    /* 관심 여부 */
    private Long isInterest;

}
