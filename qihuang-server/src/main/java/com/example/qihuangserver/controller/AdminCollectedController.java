package com.example.qihuangserver.controller;

import com.example.qihuangserver.service.AdminCollectedService;
import com.example.qihuangserver.dto.classics.AdminCollectedDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/collected")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminCollectedController {
    private final AdminCollectedService adminCollectedService;

    @GetMapping
    public ResponseEntity<Page<AdminCollectedDTO>> getAllCollections(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long classicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 参数直接传递，Service已处理null情况
        return ResponseEntity.ok(adminCollectedService.getAllCollections(userId, classicId, page, size));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
//        adminCollectedService.deleteCollection(id);
//        return ResponseEntity.noContent().build();
//    }
}