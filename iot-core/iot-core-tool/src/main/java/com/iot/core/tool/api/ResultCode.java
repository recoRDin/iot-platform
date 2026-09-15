package com.iot.core.tool.api;


//通用结果码
public enum ResultCode implements IResultCode {

    SUCCESS(200, "操作成功"),
    FAILURE(400, "业务异常"),
    PARAM_VALID_ERROR(400, "参数校验失败"),
    MSG_NOT_READABLE(400, "请求体缺失或格式错误"),
    INTERNAL_SERVER_ERROR(500, "服务器内部异常，请稍后重试"),
    PARAM_MISS(400, "缺少必要的请求参数"),
    PARAM_TYPE_ERROR(400, "请求参数类型错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有访问权限");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode(){
        return code;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
