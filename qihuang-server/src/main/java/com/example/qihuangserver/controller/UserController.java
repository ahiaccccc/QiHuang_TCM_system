package com.example.qihuangserver.controller;

import com.example.qihuangserver.dto.user.*;
import com.example.qihuangserver.service.UserService;
import com.example.qihuangserver.util.Result;
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

    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody RegisterRequest request) {
        Result result = userService.registerUser(request);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Result> login(@Valid @RequestBody LoginRequest request) {
        Result result = userService.login(request);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PostMapping("/email-reset-password")
    public ResponseEntity<Result> emailResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Result result = userService.emailResetPassword(request);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @GetMapping("/profile")
    public ResponseEntity<Result> getProfile(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Result result = userService.getProfile(token);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<Result> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Result result = userService.updateProfile(token, request);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<Result> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Result result = userService.uploadAvatar(token, file);
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Result> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Result result = userService.changePassword(token, request);
        return ResponseEntity.status(result.getCode()).body(result);
    }

}
