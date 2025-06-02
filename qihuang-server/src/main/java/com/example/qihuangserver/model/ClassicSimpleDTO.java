package com.example.qihuangserver.model;

import lombok.Data;

// ClassicSimpleDTO.java
@Data
public class ClassicSimpleDTO {
    private Long id;
    private String title;
    private String author;
    private String dynasty;
    private String coverUrl;
    private String category;
    private String summary;

    public static ClassicSimpleDTO fromEntity(Classic classic) {
        ClassicSimpleDTO dto = new ClassicSimpleDTO();
        dto.setId(classic.getId());
        dto.setTitle(classic.getTitle());
        return dto;
    }
}