package com.example.qihuangserver.dto.classics;

import lombok.Data;

@Data
public class AdminCollectedDTO {
    private Long collectedId;
    private Long userId;
    private String userName;
    private Long classicId;
    private String classicTitle;
    private String collectedTitle;
}