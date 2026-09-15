package com.iot.core.log.exception;


import com.iot.core.tool.api.IResultCode;
import com.iot.core.tool.api.ResultCode;

//业务异常
public class ServiceException extends RuntimeException {

    private final IResultCode resultCode;

    //使用自定义提示信息
    public ServiceException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILURE;
    }

    //使用统一结果码
    public ServiceException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
    //使用统一结果码并保留原始异常
    public ServiceException(IResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(),cause);
        this.resultCode = resultCode;
    }

    public IResultCode getResultCode() {
        return resultCode;
    }
}
