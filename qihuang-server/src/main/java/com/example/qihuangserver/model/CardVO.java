package com.example.qihuangserver.model;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CardVO {

    public String img;
    public String name;
    public String describe;
    public String word;

}
