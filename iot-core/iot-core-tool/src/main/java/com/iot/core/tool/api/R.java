package com.iot.core.tool.api;


//统一接口返回对象
public class R<T> {

    //业务状态码
    private final int code;
    //是否成功
    private final boolean success;
    //业务数据
    private final T data;
    //提示信息
    private final String msg;

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = code == ResultCode.SUCCESS.getCode();
    }

    //成功，并返回数据
    public static <T> R<T> data(T data) {
        return new R<T>(
                ResultCode.SUCCESS.getCode(),
                data,
                ResultCode.SUCCESS.getMessage());
    }

    //成功，不返回数据
    public static <T> R<T> success() {
        return data(null);
    }

    //使用结果码中的状态码和提示
    public static <T> R<T> fail(IResultCode resultCode) {
        return fail(resultCode, resultCode.getMessage());
    }


    public static <T> R<T> fail(IResultCode resultCode, String msg) {
        return new R<T>(
                resultCode.getCode(),
                null,
                msg);
    }

    public static <T> R<T> fail(String msg) {
        return fail(ResultCode.FAILURE, msg);
    }

    //根据操作结果返回
    public static <T> R<T> status(boolean flag) {
        return flag ? success() : fail(ResultCode.FAILURE);
    }

    public int getCode() {
        return code;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }
}
