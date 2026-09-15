package com.iot.core.log.aspect;


import com.iot.core.log.annotation.ApiLog;
import com.iot.core.log.constant.LogConstants;
import com.iot.core.log.model.LogApi;
import com.iot.core.log.publisher.ApiLogPublisher;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

//API操作日志切面
@Aspect
public class ApiLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ApiLogAspect.class);

    private final ApiLogPublisher apiLogPublisher;

    public ApiLogAspect(ApiLogPublisher apiLogPublisher) {
        this.apiLogPublisher = apiLogPublisher;
    }

    //拦截所有@ApiLog的方法
    @Around("@annotation(apiLog)")
    public Object around(ProceedingJoinPoint joinPoint, ApiLog apiLog) throws Throwable {

        long startTime = System.nanoTime();

        // 执行原Controller方法
        Object result = joinPoint.proceed();

        long duration = calculateDuration(startTime);

        LogApi logApi = new LogApi();

        // 1表示API操作日志
        logApi.setType("1");

        // @ApiLog中的操作名称
        logApi.setTitle(apiLog.value());

        // 方法执行耗时
        logApi.setTime(String.valueOf(duration));

        // 当前请求的traceId
        logApi.setTraceId(MDC.get(LogConstants.TRACE_ID));

        // 被调用的类和方法
        logApi.setMethodClass(joinPoint.getTarget().getClass().getName());

        logApi.setMethodName(joinPoint.getSignature().getName());

        logApi.setCreateTime(LocalDateTime.now());

        //HTTP请求信息
        fillRequestInfo(logApi);

        // 发布API日志事件
        apiLogPublisher.publish(logApi);

        return result;

    }


    private void fillRequestInfo(LogApi logApi) {
        if (!(RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes attributes)) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        logApi.setRequestUri(request.getRequestURI());
        logApi.setMethod(request.getMethod());
        logApi.setRemoteIp(request.getRemoteAddr());
        logApi.setUserAgent(request.getHeader("User-Agent"));
    }


    private long calculateDuration(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}


