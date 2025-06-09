package com.example.qihuangserver.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "herbs")
public class Herb {

    @Id
    private String id;

    private String name;
    private String imageUrl;
    private String description;
    private Date createdTime;
}