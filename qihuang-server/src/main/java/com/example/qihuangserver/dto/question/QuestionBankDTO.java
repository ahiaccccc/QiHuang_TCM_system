package com.example.qihuangserver.dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankDTO {
    private Integer id;
    private String question;
    private String options;
}
