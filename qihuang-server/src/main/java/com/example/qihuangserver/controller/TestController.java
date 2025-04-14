package com.example.qihuangserver.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*") // 临时允许所有跨域请求
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from Spring Boot!");
    }

    @PostMapping("/echo")
    public ResponseEntity<String> echo(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok("Echo: " + data.get("message"));
    }
}