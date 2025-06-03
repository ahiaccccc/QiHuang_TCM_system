package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.ApiResponse;
import com.example.qihuangserver.dto.zhongyizhishiwenda.CollectionsChatRequest;
import com.example.qihuangserver.repository.UserRepository;
import com.example.qihuangserver.service.CollectionsChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin(origins = "http://localhost:4000")
public class CollectionsChatController {

    private final CollectionsChatService collectionsChatService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public CollectionsChatController(CollectionsChatService collectionsChatService) {
        this.collectionsChatService = collectionsChatService;
    }


    @PostMapping
    public ApiResponse addCollection(@RequestBody CollectionsChatRequest request,
                                     @RequestParam Long userId,
                                     HttpServletRequest httpRequest) {
        // 验证token
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return new ApiResponse(false, "未授权的访问", null);
        }

        return collectionsChatService.addCollection(userId, request);
    }

    @CrossOrigin(origins = "http://localhost:4000")
    @GetMapping("/{collectionId}")
    public ApiResponse getCollectionById(@RequestParam Long userId,
                                         @PathVariable Long collectionId,HttpServletRequest request) {
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
    public ApiResponse getUserCollections(@RequestParam Long userId, HttpServletRequest request) {
        if (userId == null) {
            return new ApiResponse(false, "用户ID不能为空", null);
        }

        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            return new ApiResponse(false, "用户不存在", null);
        }

        return collectionsChatService.getUserCollections(userId);

    }
}