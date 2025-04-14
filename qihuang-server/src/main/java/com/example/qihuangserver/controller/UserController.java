package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.ApiResponse;
import com.example.qihuangserver.dto.user.LoginRequest;
import com.example.qihuangserver.dto.user.RegisterRequest;
import com.example.qihuangserver.dto.user.ResetPasswordRequest;
import com.example.qihuangserver.dto.user.UpdateProfileRequest;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.UserRepository;
import com.example.qihuangserver.service.UserService;
import com.example.qihuangserver.util.SimpleTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*") // 临时允许所有跨域请求
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse response = userService.login(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 401).body(response);
    }

    @PostMapping("/email-reset-password")
    public ResponseEntity<ApiResponse> emailResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ApiResponse response = userService.emailResetPassword(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(
            @RequestHeader("Authorization") String authHeader) {
        // 从SimpleTokenUtil验证token
        String token = authHeader.replace("Bearer ", "");
        long userId = SimpleTokenUtil.validateToken(token);

        if (userId == 0L) { // 假设无效token返回0
            return ResponseEntity.status(401).body(new ApiResponse(false, "无效Token", null));
        }

        ApiResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader("Authorization") String token) {
        long userId = SimpleTokenUtil.validateToken(token.replace("Bearer ", ""));
        if (userId == 0L) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "无效Token", null));
        }

        ApiResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<ApiResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        long userId = SimpleTokenUtil.validateToken(token.replace("Bearer ", ""));
        if (userId == 0L) {
            return ResponseEntity.status(401).build();
        }

        ApiResponse response = userService.uploadAvatar(userId, file);
        return ResponseEntity.ok(response);
    }
}