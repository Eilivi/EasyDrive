package com.peirong.entity;

import lombok.Data;

@Data
public class RestBeanResponse<T> {
    private int status;
    private boolean success;
    private T message;
    private RestBeanResponse(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }
    public static <T> RestBeanResponse<T> success() {
        return new RestBeanResponse<>(200,true,null);
    }
    public static <T> RestBeanResponse<T> success(T data) {
        return new RestBeanResponse<>(200,true, data);
    }
    public static <T> RestBeanResponse<T> failure(int status) {
        return new RestBeanResponse<>(status, false, null);
    }
    public static <T> RestBeanResponse<T> failure(int status, T data) {
        return new RestBeanResponse<>(status, false, data);
    }
}