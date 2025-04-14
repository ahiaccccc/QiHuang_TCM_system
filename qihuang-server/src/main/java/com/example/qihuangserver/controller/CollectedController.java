package com.example.qihuangserver.controller;

import com.example.qihuangserver.model.Collected;
import com.example.qihuangserver.service.CollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collected")
public class CollectedController {

    @Autowired
    private CollectedService collectedService;

    @GetMapping
    public List<Collected> getAllCollected() {
        return collectedService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collected> getCollectedById(@PathVariable Long id) {
        return collectedService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollected(@PathVariable Long id) {
        collectedService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping
    public void collect(@RequestBody Map<String, Long> payload) {
        Long classicId = payload.get("classicId");
        collectedService.collect(classicId);
    }
}