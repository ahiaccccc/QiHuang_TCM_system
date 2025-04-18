package com.example.qihuangserver.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;
    private String msg;
    private Object data;
    public static Result success(Object data) {
        return new Result(ResultCode.SUCCESS.getCode(), "success", data);
    }
    public static Result success(Object data, String message) {
        return new Result(ResultCode.SUCCESS.getCode(), message, data);
    }
    public static Result success() {
        return new Result(ResultCode.SUCCESS.getCode(), "success", null);
    }

    public static Result error(Integer code, String message) {
        return new Result(code, message, null);
    }

    public static Result error(String message) {
        return new Result(ResultCode.FAIL.getCode(), message, null);
    }

}
