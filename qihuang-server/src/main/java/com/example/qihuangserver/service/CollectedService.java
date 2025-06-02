package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Collected;
import com.example.qihuangserver.model.Classic;
import com.example.qihuangserver.model.User;
import jakarta.transaction.Transactional;
import com.example.qihuangserver.repository.CollectedRepository;
import com.example.qihuangserver.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollectedService {
    private final CollectedRepository collectedRepository;
    private final UserService userService;
    private final ClassicService classicService;
    private final UserRepository userRepository;

    public CollectedService(CollectedRepository collectedRepository, UserService userService, ClassicService classicService, UserRepository userRepository) {
        this.collectedRepository = collectedRepository;
        this.userService = userService;
        this.classicService = classicService;
        this.userRepository = userRepository;
    }

    public boolean isCollected(User user, Classic classic) {
        return collectedRepository.findByUserAndClassic(user, classic).isPresent();
    }

    @Transactional
    public void toggleCollected(Long userId, Long classicId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));;
        Classic classic = classicService.getClassicById(classicId);
        Optional<Collected> collected = collectedRepository.findByUserAndClassic(user, classic);
        if (collected.isPresent()) {
            collectedRepository.deleteByUserAndClassic(user, classic);
        } else {
            Collected newCollected = new Collected();
            newCollected.setUser(user);
            newCollected.setClassic(classic);
            newCollected.setTitle(title);
            collectedRepository.save(newCollected);
        }
    }
    public List<Collected> getUserCollections(User user) {
        return collectedRepository.findByUser(user);
    }
}
