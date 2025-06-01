package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.ApiResponse;
import com.example.qihuangserver.dto.zhongyizhishiwenda.CollectionsChatRequest;
import com.example.qihuangserver.service.CollectionsChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin(origins = "http://localhost:4000")
public class CollectionsChatController {

    private final CollectionsChatService collectionsChatService;

    @Autowired
    public CollectionsChatController(CollectionsChatService collectionsChatService) {
        this.collectionsChatService = collectionsChatService;
    }


    @PostMapping
    public ApiResponse addCollection(@RequestParam Long userId,
                                     @RequestBody @Valid CollectionsChatRequest request) {
        return collectionsChatService.addCollection(userId, request);
    }


    @CrossOrigin(origins = "http://localhost:4000")
    @GetMapping("/{collectionId}")
    public ApiResponse getCollectionById(@RequestParam Long userId,
                                         @PathVariable Long collectionId) {
        return collectionsChatService.getCollectionById(userId, collectionId);
    }

    @CrossOrigin(origins = "http://localhost:4000")
    @DeleteMapping("/{collectionId}")
    public ApiResponse deleteCollection(@RequestParam Long userId,
                                        @PathVariable Long collectionId) {
        try {
            // 添加参数验证
            if (userId == null || collectionId == null) {
                return new ApiResponse(false, "用户ID或收藏ID不能为空", null);
            }

            return collectionsChatService.deleteCollection(userId, collectionId);
        } catch (Exception e) {
            return new ApiResponse(false, "删除收藏时发生错误: " + e.getMessage(), null);
        }
    }





    @CrossOrigin(origins = "http://localhost:4000")
    @GetMapping
    public ApiResponse getUserCollections(@RequestParam Long userId) {
        return collectionsChatService.getUserCollections(userId);
    }
}