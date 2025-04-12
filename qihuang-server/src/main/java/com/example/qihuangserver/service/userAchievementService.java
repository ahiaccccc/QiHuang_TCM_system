package com.example.qihuangserver.service;

import com.example.qihuangserver.model.userAchievement;
import com.example.qihuangserver.repository.userAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class userAchievementService {

    @Autowired
    private userAchievementRepository userAchievementRepository;


    public Optional<userAchievement> findById(Integer id) {
        return userAchievementRepository.findById(id);
    }

    public userAchievement save(userAchievement userAchievement) {return userAchievementRepository.save(userAchievement);}

    public void deleteById(Integer id) {
        userAchievementRepository.deleteById(id);
    }

}
