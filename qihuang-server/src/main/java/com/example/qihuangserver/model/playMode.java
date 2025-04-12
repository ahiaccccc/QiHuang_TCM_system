package com.example.qihuangserver.model;

//刷题模式
public enum playMode {
    PRACTICE("排位"),
    RANK("刷题");

    private String desc;

    playMode (String desc){
        this.desc = desc;
    }
    public String getDesc(){
        return desc;
    }
}
