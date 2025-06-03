package com.example.qihuangserver.service;

import com.example.qihuangserver.dto.ApiResponse;
import com.example.qihuangserver.dto.zhongyizhishiwenda.CollectionsChatRequest;
import com.example.qihuangserver.exception.ResourceNotFoundException;
import com.example.qihuangserver.model.CollectionsChat;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.CollectionsChatRepository;
import com.example.qihuangserver.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectionsChatService {

    private final CollectionsChatRepository collectionsChatRepository;
    private final UserRepository userRepository;

    @Autowired
    public CollectionsChatService(CollectionsChatRepository collectionsChatRepository,
                                  UserRepository userRepository) {
        this.collectionsChatRepository = collectionsChatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApiResponse addCollection(Long userId, CollectionsChatRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));



        CollectionsChat collection = new CollectionsChat();
        collection.setUser(user);
        collection.setContent(request.getContent());
        collection.setCreatedAt(Instant.now());

        collectionsChatRepository.save(collection);
        return new ApiResponse(true, "收藏成功",collection.getId());
    }

    @Transactional(readOnly = true)
    public ApiResponse getCollectionById(Long userId, Long collectionId) {
        try {
            // Verify user exists
            userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

            // Find the collection
            List<CollectionsChat> collections = collectionsChatRepository.findByCollectionIdAndUserId(userId,collectionId);

            // 转换为DTO列表返回
            List<Object> result = collections.stream()
                    .map(collection -> {
                        return new HashMap<String, Object>() {{
                            put("collectionId", collection.getId());
                            put("content", collection.getContent());
                            put("createdAt", collection.getCreatedAt());
                        }};
                    })
                    .collect(Collectors.toList());

            return new ApiResponse(true, "获取收藏成功", result);
        } catch (ResourceNotFoundException e) {
            return new ApiResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse(false, "获取收藏时发生错误", null);
        }
    }
    // 服务层方法
    @Transactional
    public ApiResponse deleteCollection(Long userId, Long collectionId) {
        try {
            // 添加额外验证
            if (userId == null || collectionId == null) {
                return new ApiResponse(false, "参数不能为空", null);
            }

            CollectionsChat collection = collectionsChatRepository.findById(collectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("收藏记录不存在"));


            collectionsChatRepository.delete(collection);
            return new ApiResponse(true, "删除收藏成功", null);
        } catch (ResourceNotFoundException e) {
            return new ApiResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse(false, "删除收藏时发生错误", null);
        }
    }


    @Transactional(readOnly = true)
    public ApiResponse getUserCollections(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        List<CollectionsChat> collections = collectionsChatRepository.findByUserOrderByCreatedAtDesc(user);

        // 转换为DTO列表返回
        List<Object> result = collections.stream()
                .map(collection -> {
                    return new HashMap<String, Object>() {{
                        put("collectionId", collection.getId());
                        put("content", collection.getContent());
                        put("createdAt", collection.getCreatedAt());
                    }};
                })
                .collect(Collectors.toList());

        return new ApiResponse(true, "获取收藏列表成功",result);
    }
}