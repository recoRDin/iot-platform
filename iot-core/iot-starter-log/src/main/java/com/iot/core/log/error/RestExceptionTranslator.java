package com.iot.core.log.error;

import com.iot.core.log.exception.ServiceException;
import com.iot.core.tool.api.R;
import com.iot.core.tool.api.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolationException;
//将 MVC 请求中的业务异常转换为统一返回结果。
@RestControllerAdvice
public class RestExceptionTranslator {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionTranslator.class);


    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<R<Void>> handleServiceException(ServiceException exception) {

        log.warn("业务处理失败", exception);

        HttpStatus status = HttpStatus.resolve(exception.getResultCode().getCode());
        if (status == null || !status.isError()) {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status)
                .body(R.fail(exception.getResultCode(), exception.getMessage()));

    }

    //前端未传或无法解析json
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMessageNotReadable(HttpMessageNotReadableException exception) {

        log.warn("请求体读取失败：{}", exception.getClass().getSimpleName());

        return R.fail(ResultCode.MSG_NOT_READABLE);
    }

    //参数校验异常处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {

        log.warn("请求参数校验失败");

        // 默认提示
        String message = ResultCode.PARAM_VALID_ERROR.getMessage();

        // 获取第一个字段错误
        FieldError fieldError = exception.getBindingResult().getFieldError();

        if (fieldError != null
                && !fieldError.isBindingFailure()
                && fieldError.getDefaultMessage() != null) {
            message = fieldError.getDefaultMessage();
        }

        return R.fail(ResultCode.PARAM_VALID_ERROR, message);
    }

    //缺少请求参数
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMissingRequestParameter(MissingServletRequestParameterException exception) {

        log.warn("缺少请求参数：{}", exception.getParameterName());

        return R.fail(ResultCode.PARAM_MISS);
    }

    //参数类型错误
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {

        log.warn("请求参数类型错误，参数名：{}", exception.getName());

        return R.fail(ResultCode.PARAM_TYPE_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolation(
            ConstraintViolationException exception) {

        log.warn("请求参数校验失败");

        String message = exception.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse(ResultCode.PARAM_VALID_ERROR.getMessage());

        return R.fail(ResultCode.PARAM_VALID_ERROR, message);
    }

    //未知系统异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleUnexpectedException(Exception exception) throws Exception {

        if (exception instanceof ErrorResponse
                || exception instanceof BindException
                || exception instanceof TypeMismatchException
                || AnnotatedElementUtils.hasAnnotation(
                exception.getClass(), ResponseStatus.class)) {
            throw exception;
        }

        // 详细原因和堆栈留在后端日志中
        log.error("服务器处理请求时发生异常", exception);

        return R.fail(ResultCode.INTERNAL_SERVER_ERROR);
    }
}
