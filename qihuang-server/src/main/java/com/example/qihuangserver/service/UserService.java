package com.example.qihuangserver.service;

import com.example.qihuangserver.dto.ApiResponse;
import com.example.qihuangserver.dto.user.LoginRequest;
import com.example.qihuangserver.dto.user.RegisterRequest;
import com.example.qihuangserver.dto.user.ResetPasswordRequest;
import com.example.qihuangserver.dto.user.UpdateProfileRequest;
import com.example.qihuangserver.exception.InvalidCredentialsException;
import com.example.qihuangserver.exception.UserAlreadyExistsException;
import com.example.qihuangserver.exception.UserNotFoundException;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.UserRepository;
import com.example.qihuangserver.util.PasswordUtil;
import com.example.qihuangserver.util.SimpleTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        // 验证两次密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("用户名已存在");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        // 使用自定义工具类加密密码
//        user.setPassword(PasswordUtil.encryptPassword(registerRequest.getPassword()));
        user.setPassword(registerRequest.getPassword());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public ApiResponse login(LoginRequest loginRequest) {
        try {
            // 1. 查找用户
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("邮箱未注册"));

            // 2. 验证密码
//            if (!PasswordUtil.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
            if (!loginRequest.getPassword().equals(user.getPassword())) {
                throw new InvalidCredentialsException("密码错误");
            }

            // 3. 生成JWT token
//            String token = JwtUtil.generateToken(user.getUserId());
            String token = SimpleTokenUtil.generateToken(user.getUserId());

            // 4. 构造返回数据
            Map<String, Object> userData = new HashMap<>();
//            userData.put("token", token);
            userData.put("userId", user.getUserId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("avatar", user.getAvatar());
            userData.put("token", token);

            return new ApiResponse(true, "登录成功", userData);

        } catch (UserNotFoundException | InvalidCredentialsException e) {
            return new ApiResponse(false, e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponse emailResetPassword(ResetPasswordRequest request) {
        try {
            // 1. 查找用户
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("该邮箱未注册"));

            // 2. 生成随机密码
            String newPassword = PasswordUtil.generateRandomPassword();
//            user.setPassword(PasswordUtil.encryptPassword(newPassword));
            user.setPassword(newPassword);
            userRepository.save(user);

            // 3. 发送邮件
            sendResetEmail(user.getEmail(), newPassword);

            return new ApiResponse(true, "密码已重置，请查收邮件", null);
        } catch (UserNotFoundException e) {
            return new ApiResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse(false, "邮件发送失败，请稍后重试", null);
        }
    }

    private void sendResetEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("zyhcc04@126.com");
        message.setTo(toEmail);
        message.setSubject("【系统】密码重置通知");
        message.setText(
                "您的账户密码已重置：\n\n" +
                        "新密码：" + newPassword + "\n\n" +
                        "请及时登录系统修改密码\n" +
                        "（本邮件由系统自动发送，请勿回复）"
        );
        mailSender.send(message);
    }

    @Transactional(readOnly = true)
    public ApiResponse getProfile(long userId) { // 注意改为基本类型long
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("用户不存在"));

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("userId", user.getUserId());
        profileData.put("username", user.getUsername());
        profileData.put("email", user.getEmail());
        profileData.put("avatar", user.getAvatar());

        return new ApiResponse(true, "获取个人信息成功", profileData);
    }

    @Transactional
    public ApiResponse updateProfile(long userId, UpdateProfileRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("用户不存在"));

            // 验证邮箱唯一性
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("邮箱已被注册");
            }

            // 验证用户名唯一性
            if (!user.getUsername().equals(request.getUsername()) &&
                    userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("用户名已存在");
            }

            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            userRepository.save(user);

            return new ApiResponse(true, "更新成功", null);
        } catch (UserNotFoundException | UserAlreadyExistsException e) {
            return new ApiResponse(false, e.getMessage(), null);
        }
    }

    public ApiResponse uploadAvatar(long userId, MultipartFile file) {
        try {
            // 1. 验证用户存在
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("用户不存在"));

            // 2. 获取用户当前头像路径
            String currentAvatarPath = user.getAvatar();

            // 2. 保存目录
            String saveDir = "classpath:/static/avatars/";
            String accessPath = "/avatars/";

            // 3. 如果存在历史头像，删除旧头像
            if (currentAvatarPath != null && !currentAvatarPath.isEmpty()) {
                // 构建旧头像的完整路径
                Path oldAvatarPath = Paths.get(saveDir, currentAvatarPath.replace(accessPath, ""));
                // 删除旧头像文件
                Files.deleteIfExists(oldAvatarPath);
            }

            // 2. 上传目录
            Path uploadPath = Paths.get(saveDir).toAbsolutePath().normalize();

            // 3. 生成唯一文件名
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = userId + "_" + System.currentTimeMillis() + "." + fileExtension;

            // 4. 保存文件
            Path targetLocation = uploadPath.resolve(fileName);
            file.transferTo(targetLocation);

            // 5. 更新用户头像路径（使用可访问的web路径）

            String avatarPath = accessPath + fileName;
            user.setAvatar(avatarPath);
            userRepository.save(user);

            return new ApiResponse(true, "头像上传成功", avatarPath);
        } catch (IOException ex) {
            return new ApiResponse(false, "文件存储失败: " + ex.getMessage(), null);
        } catch (Exception ex) {
            return new ApiResponse(false, "头像上传失败: " + ex.getMessage(), null);
        }
    }

}