package com.peirong.entity;

import lombok.Data;

@Data
public class RestResponse<T> {
    private int status;
    private boolean success;
    private T message;
    private RestResponse(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }
    public static <T> RestResponse<T> success() {
        return new RestResponse<>(200,true,null);
    }
    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(200,true, data);
    }
    public static <T> RestResponse<T> failure(int status) {
        return new RestResponse<>(status, false, null);
    }
    public static <T> RestResponse<T> failure(int status, T data) {
        return new RestResponse<>(status, false, data);
    }
}