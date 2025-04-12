package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.herb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Repository
public interface herbRepository extends MongoRepository<herb, Integer> {
    Optional<herb> findById(Integer id);

    herb save(herb herb);

    void deleteById(Integer id);

}
