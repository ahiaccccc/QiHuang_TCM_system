package com.example.qihuangserver.service;

import com.baidu.aip.imageclassify.AipImageClassify;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

@Service
public class BaiduImageRecognitionService {

    @Value("${baidu.aip.app-id}")
    private String appId;

    @Value("${baidu.aip.api-key}")
    private String apiKey;

    @Value("${baidu.aip.secret-key}")
    private String secretKey;

    private AipImageClassify client;

    @PostConstruct
    public void init() {
        client = new AipImageClassify(appId, apiKey, secretKey);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }

    /**
     * 通用物体识别
     */
    public String recognizeGeneralImage(MultipartFile file) throws IOException {
        JSONObject res = client.advancedGeneral(file.getBytes(), new HashMap<>());
        return res.toString(2);
    }

    /**
     * 菜品识别
     */
    public String recognizeDishImage(MultipartFile file) throws IOException {
        JSONObject res = client.dishDetect(file.getBytes(), new HashMap<>());
        return res.toString(2);
    }

    /**
     * 植物识别
     */
    public String recognizePlantImage(MultipartFile file) throws IOException {
        JSONObject res = client.plantDetect(file.getBytes(), new HashMap<>());
        return res.toString(2);
    }

    /**
     * 动物识别
     */
    public String recognizeAnimalImage(MultipartFile file) throws IOException {
        JSONObject res = client.animalDetect(file.getBytes(), new HashMap<>());
        return res.toString(2);
    }
}