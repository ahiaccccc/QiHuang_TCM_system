package com.example.qihuangserver.model;

//刷题模式
public enum PlayMode {
    PRACTICE("排位"),
    RANK("刷题");

    private String desc;

    PlayMode(String desc){
        this.desc = desc;
    }
    public String getDesc(){
        return desc;
    }
}
