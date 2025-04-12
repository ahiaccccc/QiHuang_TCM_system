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
@Table(name = "answer_record")
public class answerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "number")
    private Integer number;

    @Column(name = "correct")
    private Integer correct;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", columnDefinition = "ENUM('practice','rank')")
    private playMode mode;

    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;


}
