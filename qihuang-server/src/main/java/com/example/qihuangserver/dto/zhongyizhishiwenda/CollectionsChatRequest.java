package com.example.qihuangserver.dto.zhongyizhishiwenda;

import com.example.qihuangserver.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionsChatRequest {
    @NotBlank(message = "内容不能为空")
    private String content;

    private Long id;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public @NotBlank(message = "内容不能为空") String getContent() {
        return content;
    }

    public void setContent(@NotBlank(message = "内容不能为空") String content) {
        this.content = content;
    }
}