package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.userAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userAchievementRepository extends JpaRepository<userAchievement, Integer> {
    Optional<userAchievement> findById(Integer id);

    userAchievement save(userAchievement userAchievement);

    void deleteById(Integer id);

}
