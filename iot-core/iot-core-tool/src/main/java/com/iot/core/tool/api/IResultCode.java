package com.iot.core.tool.api;


//统一结果码接口
public interface IResultCode {

    //获取业务状态码
    int getCode();

    //获取默认提示信息
    String getMessage();
}
