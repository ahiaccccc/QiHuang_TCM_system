package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Collected;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectedRepository extends JpaRepository<Collected, Long> {
    // 这里可以添加自定义查询方法
}