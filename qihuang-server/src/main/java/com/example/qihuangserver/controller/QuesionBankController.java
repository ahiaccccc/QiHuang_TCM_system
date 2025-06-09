package com.example.qihuangserver.controller;


import com.example.qihuangserver.dto.question.QuestionBankDTO;
import com.example.qihuangserver.model.QuestionBank;
import com.example.qihuangserver.service.QuestionBankService;
import com.example.qihuangserver.util.DataRequest;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/questionBank")
@CrossOrigin(origins = "*")
public class QuesionBankController {
    @Resource
    private QuestionBankService questionBankService;

    @PostMapping("findQuestionByClassId")
    public ResponseEntity<List<QuestionBank>> findQuestionByClassId(@RequestBody DataRequest dataRequest){
        Integer classId = dataRequest.getInteger("classId");
        List<QuestionBank> questions = questionBankService.findByClassId(classId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("findRandomQuestionByClassId")
    public ResponseEntity<List<QuestionBankDTO>> findRandomQuestionByClassId(@RequestBody DataRequest dataRequest){
        Integer classId = dataRequest.getInteger("classId");
        Integer limit = dataRequest.getInteger("limit");
        List<QuestionBankDTO> questions = questionBankService.findRandomByClassId(classId, limit);
        return ResponseEntity.ok(questions);
    }

}
