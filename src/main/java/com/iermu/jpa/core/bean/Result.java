package com.iermu.jpa.core.bean;

public class Result {
    private int code;
    private String description;
    private Object data;

    public static Result success() {
        return success(null);
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setDescription("success");
        result.setData(data);
        return result;
    }

    public static Result error() {
        return error(null);
    }

    public static Result error(String description) {
        return error(201, description);
    }

    public static Result error(int code, String description) {
        Result result = new Result();
        result.setCode(code);
        result.setDescription(description);
        result.setData(null);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
