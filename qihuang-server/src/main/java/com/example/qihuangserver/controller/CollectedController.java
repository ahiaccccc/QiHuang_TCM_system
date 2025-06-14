package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.classics.CollectedDTO;
import com.example.qihuangserver.model.Book;
import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.Collected;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.service.ClassicService;
import com.example.qihuangserver.service.CollectedService;
import com.example.qihuangserver.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collected")
@CrossOrigin(origins = "*")
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
    public List<CollectedDTO> getUserCollections(@RequestParam Long userId) {
        List<Collected> collections = collectedService.getUserCollections(userService.getUserById(userId));

        return collections.stream().map(collected -> {
            CollectedDTO dto = new CollectedDTO();
            dto.setCollectedId(collected.getId());
            dto.setTitle(collected.getTitle());

            Classic classic = collected.getClassic();
            dto.setClassicId(classic.getId());

            Book book = classic.getBook();
            dto.setBookId(book.getId());
            dto.setBookName(book.getName()); // 假设Book有title字段

            dto.setUserId(userId);
            return dto;
        }).collect(Collectors.toList());
    }

}
