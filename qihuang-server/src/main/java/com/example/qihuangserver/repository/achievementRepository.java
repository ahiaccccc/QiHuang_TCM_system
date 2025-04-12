package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface achievementRepository extends JpaRepository<achievement, Integer> {
    Optional<achievement> findById(Integer id);

    achievement save(achievement achievement);

    void deleteById(Integer id);

}
