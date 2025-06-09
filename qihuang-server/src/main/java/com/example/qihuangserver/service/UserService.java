// UserService.java
package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.User;
import com.example.qihuangserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 创建或更新用户
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 根据ID查询用户
    public User getUserById(Long id) {
        Optional<User> optionalUser =userRepository.findById(id);
        User user = optionalUser.orElse(new User());
        return user;
    }

    // 删除用户
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}