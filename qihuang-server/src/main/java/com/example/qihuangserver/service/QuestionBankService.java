package com.example.qihuangserver.service;

import com.example.qihuangserver.model.QuestionBank;
import com.example.qihuangserver.repository.QuestionBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.qihuangserver.dto.question.QuestionBankDTO;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuestionBankService {

    @Autowired
    private QuestionBankRepository questionBankRepository;


    public Optional<QuestionBank> findById(Integer id) {
        return questionBankRepository.findById(id);
    }

    public QuestionBank save(QuestionBank questionBank) {
        return questionBankRepository.save(questionBank);
    }

    public void deleteById(Integer id) {
        questionBankRepository.deleteById(id);
    }

    public List<QuestionBank> findByClassId(Integer classId) {return questionBankRepository.findByClassId(classId); }

    /**
     * 随机生成limit道某个类别的题目，只返回题目不返回答案和解析
     * @param classId
     * @param limit
     * @return
     */
    public List<QuestionBankDTO> findRandomByClassId(Integer classId, Integer limit) {
        return questionBankRepository.findRandomByClassId(classId, limit);
    }

    public List<QuestionBankDTO> getQuestionByQuestionStr(String questionStr){
        //所有的问题id，并以','分隔开，拼接成一个字符串questionStr，现在要将它分开，并且·逐个查询
//        if (questionStr != null && !questionStr.isEmpty()) {
            String[] questionIds = questionStr.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String idStr : questionIds) {
                try {
                    Integer id = Integer.parseInt(idStr);
                    idList.add(id);
                } catch (NumberFormatException e) {
                    System.err.println("无法将 " + idStr + " 转换为整数: " + e.getMessage());
                }
            }
            List<QuestionBankDTO> result = questionBankRepository.findByIds(idList);
        // 创建ID到DTO的映射
            Map<Integer, QuestionBankDTO> idToDto = result.stream()
                .collect(Collectors.toMap(QuestionBankDTO::getId, Function.identity()));
        return idList.stream()
                .map(idToDto::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }


}
