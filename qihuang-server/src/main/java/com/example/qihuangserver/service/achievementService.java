package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Achievement;
import com.example.qihuangserver.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class achievementService {

    @Autowired
    private AchievementRepository achievementRepository;


    public Optional<Achievement> findById(Integer id) {
        return achievementRepository.findById(id);
    }

    public Achievement save(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    public void deleteById(Integer id) {
        achievementRepository.deleteById(id);
    }

}
