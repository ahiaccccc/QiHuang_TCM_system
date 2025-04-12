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
@Table(name = "question_bank")
public class questionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @Column(name = "correct_answer", length = 255)
    private String correctAnswer;

    @Column(name = "options", length = 255)
    private String options;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
}
