package com.example.qihuangserver.service;

import com.example.qihuangserver.dto.user.LoginRequest;
import com.example.qihuangserver.dto.user.RegisterRequest;
import com.example.qihuangserver.dto.user.ResetPasswordRequest;
import com.example.qihuangserver.dto.user.UpdateProfileRequest;
import com.example.qihuangserver.exception.UserNotFoundException;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.UserRepository;
import com.example.qihuangserver.util.JWTUtils;
import com.example.qihuangserver.util.PasswordUtil;
import com.example.qihuangserver.util.Result;
import com.example.qihuangserver.util.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${avatar.upload.dir}")
    private String uploadDir;

    @Value("${avatar.access.path}")
    private String accessPath;

    @Autowired
    public UserService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public User getUserById(Long id) {
        Optional<User> optionalUser =userRepository.findById(id);
        User user = optionalUser.orElse(new User());
        return user;
    }

    @Transactional
    public Result registerUser(RegisterRequest registerRequest) {
        try {
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                return Result.error(ResultCode.ELSE_FAILED.getCode(), "两次输入的密码不一致");
            }

            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return Result.error(ResultCode.ELSE_FAILED.getCode(), "用户名已存在");
            }

            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return Result.error(ResultCode.ELSE_FAILED.getCode(), "邮箱已被注册");
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());

            userRepository.save(user);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Result login(LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);
            if (user == null) {
                return Result.error(ResultCode.ELSE_FAILED.getCode(), "邮箱未注册");
            }

            if (!loginRequest.getPassword().equals(user.getPassword())) {
                return Result.error(ResultCode.ELSE_FAILED.getCode(), "密码错误");
            }

            String token = JWTUtils.createJWT(user);

            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUserId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("avatar", user.getAvatar());
            userData.put("token", token);

            return Result.success(userData, "登录成功");
        }  catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
    }

    @Transactional
    public Result emailResetPassword(ResetPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("该邮箱未注册"));

            String newPassword = PasswordUtil.generateRandomPassword();
            user.setPassword(newPassword);
            userRepository.save(user);

            sendResetEmail(user.getEmail(), newPassword);

            return Result.success("密码已重置，请查收邮件");
        } catch (UserNotFoundException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("邮件发送失败，请稍后重试");
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
    public Result getProfile(String token) {
        try {
            long userId = JWTUtils.getUserIdFromJwtToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("用户不存在"));

            Map<String, Object> profileData = new HashMap<>();
            profileData.put("userId", user.getUserId());
            profileData.put("username", user.getUsername());
            profileData.put("email", user.getEmail());
            profileData.put("avatar", user.getAvatar());

            return Result.success(profileData, "获取个人信息成功");
        } catch (Exception e) {
            return Result.error(ResultCode.UNAUTHORIZED.getCode(), "无效Token");
        }
    }

    @Transactional
    public Result updateProfile(String token, UpdateProfileRequest request) {
        try {
            long userId = JWTUtils.getUserIdFromJwtToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("用户不存在"));

            if (!user.getUsername().equals(request.getUsername()) &&
                    userRepository.existsByUsername(request.getUsername())) {
                return Result.error(ResultCode.ELSE_FAILED.getCode(), "用户名已存在");
            }

            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            userRepository.save(user);

            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(ResultCode.UNAUTHORIZED.getCode(), "无效Token");
        }
    }

    public Result uploadAvatar(String token, MultipartFile file) {
        try {
            long userId = JWTUtils.getUserIdFromJwtToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("用户不存在"));

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                String oldAvatarPath = user.getAvatar().replace(accessPath, ""); // 移除访问前缀
                Path oldAvatarFilePath = uploadPath.resolve(oldAvatarPath).normalize();

                if (!oldAvatarFilePath.startsWith(uploadPath)) {
                    throw new SecurityException("无效的文件路径");
                }

                try {
                    Files.deleteIfExists(oldAvatarFilePath);
                } catch (IOException e) {
                    return Result.error("头像上传失败: " + e.getMessage());
                }
            }

            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = userId + "_" + System.currentTimeMillis() + "." + fileExtension;

            Path targetLocation = uploadPath.resolve(fileName);
            file.transferTo(targetLocation);

            String avatarPath = accessPath + fileName;
            user.setAvatar(avatarPath);
            userRepository.save(user);

            return Result.success(avatarPath, "头像上传成功");
        } catch (IOException ex) {
            return Result.error("文件存储失败: " + ex.getMessage());
        } catch (Exception ex) {
            return Result.error("头像上传失败: " + ex.getMessage());
        }
    }
}
