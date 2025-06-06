package com.example.qihuangserver.dto.classics;

import com.example.qihuangserver.model.Classic;
import lombok.Data;

// ClassicSimpleDTO.java
@Data
public class ClassicSimpleDTO {
    private Long id;
    private String title;

    public static ClassicSimpleDTO fromEntity(Classic classic) {
        ClassicSimpleDTO dto = new ClassicSimpleDTO();
        dto.setId(classic.getId());
        dto.setTitle(classic.getTitle());
        return dto;
    }
}