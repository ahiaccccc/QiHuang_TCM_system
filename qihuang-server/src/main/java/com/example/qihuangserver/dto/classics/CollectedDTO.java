package com.example.qihuangserver.dto.classics;

import lombok.Data;

@Data
public class CollectedDTO {
    private Long collectedId;
    private Long userId;
    private Long bookId;
    private String bookName;
    private Long classicId;
    private String title;
}