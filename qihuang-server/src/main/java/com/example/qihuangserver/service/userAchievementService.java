package com.example.qihuangserver.service;

import com.example.qihuangserver.model.UserAchievement;
import com.example.qihuangserver.repository.UserAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class userAchievementService {

    @Autowired
    private UserAchievementRepository userAchievementRepository;


    public Optional<UserAchievement> findById(Integer id) {
        return userAchievementRepository.findById(id);
    }

    public UserAchievement save(UserAchievement userAchievement) {return userAchievementRepository.save(userAchievement);}

    public void deleteById(Integer id) {
        userAchievementRepository.deleteById(id);
    }

}
