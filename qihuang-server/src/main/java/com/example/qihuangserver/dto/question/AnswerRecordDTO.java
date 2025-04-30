package com.example.qihuangserver.dto.question;

import com.example.qihuangserver.model.PlayMode;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerRecordDTO {
    private Integer id;
    private Integer userId;
    private Integer number;
    private Integer classId;

    private String correct;

    private PlayMode playMode;

    private List<QuestionBankDTO> questions;

    private String answers;

    private Integer activating;

    private LocalDateTime time;


}
