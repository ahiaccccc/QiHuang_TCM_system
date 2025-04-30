package com.example.qihuangserver.repository;

import com.example.qihuangserver.model.Herb;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Repository
public interface HerbRepository extends MongoRepository<Herb, Integer> {
    Optional<Herb> findById(Integer id);

    Herb save(Herb herb);

    void deleteById(Integer id);

}
