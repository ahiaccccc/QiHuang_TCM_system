package com.example.qihuangserver.model;

import com.example.qihuangserver.dto.question.AnswerRecordDTO;
import lombok.*;
import org.springframework.stereotype.Component;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "answer_record")
public class AnswerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column
    private Integer classId;

    @Column
    private Integer number;

    //用01串表示每一道题是错还是对
    @Column(name = "correct")
    private String correct;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", columnDefinition = "ENUM('PRACTICE','RANK')")
    private PlayMode playMode;

    @Column
    private String questions;

    @Column
    private String answers;

    @Column
    private Integer activating;

    @Column(name = "time", columnDefinition = "datetime(0)")
    private LocalDateTime time;

    public AnswerRecordDTO getDTOExceptQuestions(){

        return AnswerRecordDTO.builder()
                .id(id)
                .userId(userId)
                .number(number)
                .classId(classId)
                .correct(correct)
                .playMode(playMode)
                .activating(activating)
                .answers(answers)
                .time(time)
                .build();
    }

}
