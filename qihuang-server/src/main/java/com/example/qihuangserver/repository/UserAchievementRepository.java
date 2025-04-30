package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Integer> {
    Optional<UserAchievement> findById(Integer id);

    UserAchievement save(UserAchievement userAchievement);

    void deleteById(Integer id);

}
