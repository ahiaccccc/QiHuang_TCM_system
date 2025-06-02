package com.example.qihuangserver.model;

import lombok.*;
import org.springframework.stereotype.Component;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ranking_record")
public class RankRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column
    private Integer recordId;

    @Column
    private Integer number;



    @Column(name = "score")
    private double score;

    @Column(name = "time")
//    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time;
}
