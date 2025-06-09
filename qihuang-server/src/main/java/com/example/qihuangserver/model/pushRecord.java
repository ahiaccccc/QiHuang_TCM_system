package com.example.qihuangserver.model;

import lombok.*;
import org.springframework.stereotype.Component;
import jakarta.persistence.*;
import java.util.Date;
@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "push_record")
public class pushRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "herb_id")
    private Integer herbId;

    @Column(name = "push_date")
    @Temporal(TemporalType.DATE)
    private Date pushDate;

    @Column(name = "blessing", columnDefinition = "TEXT")
    private String blessing;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
}
