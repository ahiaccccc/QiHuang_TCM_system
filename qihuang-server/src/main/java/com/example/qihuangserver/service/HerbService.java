package com.example.qihuangserver.service;

import com.example.qihuangserver.model.Herb;
import com.example.qihuangserver.repository.HerbRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HerbService {

//    @Resource
//    private HerbRepository herbRepository;

//    public Herb getRendomHerb() {
//        List <Herb> herbList = herbRepository.findAll();
//        int randomIndex = (int)(Math.random()* herbList.size());
//        Herb herb = herbList.get(randomIndex);
//        return herb;
//    }

    public Optional<Herb> findById(String id) {
        return herbRepository.findById(id);
    }

    public Herb save(Herb herb) {
        return herbRepository.save(herb);
    }

    public void deleteById(Integer id) {
        herbRepository.deleteById(id);
    }

    private final MongoTemplate mongoTemplate;
    private final HerbRepository herbRepository;

    public HerbService(MongoTemplate mongoTemplate, HerbRepository herbRepository) {
        this.mongoTemplate = mongoTemplate;
        this.herbRepository = herbRepository;
    }

    public Optional<Herb> getRandomHerb() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sample(1)
        );
        AggregationResults<Herb> results = mongoTemplate.aggregate(
                aggregation,        // 聚合管道
                "herbs",            // 集合名称（与实体类@Document标注的一致）
                Herb.class          // 实体类
        );

        return Optional.ofNullable(results.getUniqueMappedResult());
    }

}
