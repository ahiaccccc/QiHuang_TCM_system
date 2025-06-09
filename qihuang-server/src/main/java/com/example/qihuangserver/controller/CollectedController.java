package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.Collected;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.service.ClassicService;
import com.example.qihuangserver.service.CollectedService;
import com.example.qihuangserver.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collected")
public class CollectedController {
    private final CollectedService collectedService;
    private final UserService userService;
    private final ClassicService classicService;

    public CollectedController(CollectedService collectedService, UserService userService, ClassicService classicService) {
        this.collectedService = collectedService;
        this.userService = userService;
        this.classicService = classicService;
    }

    @GetMapping("/status")
    public boolean isCollected(@RequestParam Long userId, @RequestParam Long classicId) {
        User user = userService.getUserById(userId);
        Classic classic = classicService.getClassicById(classicId);
        return collectedService.isCollected(user, classic);
    }

    @PostMapping("/toggle")
    public void toggleCollected(@RequestParam Long userId, @RequestParam Long classicId, @RequestParam String title) {
        collectedService.toggleCollected(userId, classicId, title);
    }

    @GetMapping("/list")
    public List<Collected> getUserCollections(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }
        return collectedService.getUserCollections(user);
    }

}
