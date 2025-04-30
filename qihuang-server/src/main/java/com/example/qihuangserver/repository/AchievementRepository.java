package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    Optional<Achievement> findById(Integer id);

    Achievement save(Achievement achievement);

    void deleteById(Integer id);

}
