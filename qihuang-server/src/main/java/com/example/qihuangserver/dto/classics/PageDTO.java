package com.example.qihuangserver.dto.classics;

import lombok.Data;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

// PageDTO.java
@Data
public class PageDTO<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T, E> PageDTO<T> fromPage(Page<E> page, Function<E, T> converter) {
        PageDTO<T> dto = new PageDTO<>();
        dto.setContent(page.getContent().stream()
                .map(converter)
                .collect(Collectors.toList()));
        dto.setPage(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setLast(page.isLast());
        return dto;
    }
}