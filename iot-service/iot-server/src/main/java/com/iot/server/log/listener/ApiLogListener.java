package com.iot.server.log.listener;

import com.iot.core.log.constant.EventConstants;
import com.iot.core.log.event.ApiLogEvent;
import com.iot.core.log.model.LogApi;
import com.iot.server.log.service.ApiLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

public class ApiLogListener {

    private static final Logger log = LoggerFactory.getLogger(ApiLogListener.class);

    private final ApiLogService apiLogService;

    public ApiLogListener(ApiLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    @Async("apiLogTaskExecutor")
    @EventListener
    public void saveApiLog(ApiLogEvent event) {
        Object source = event.getSource();

        if (!(source instanceof Map<?, ?> eventData)) {
            log.warn("API日志事件数据格式错误");
            return;
        }

        Object value = eventData.get(EventConstants.EVENT_LOG);

        if (!(value instanceof LogApi apiLog)) {
            log.warn("API日志事件中不存在LogApi数据");
            return;
        }

        try {

            //异步写入数据库
            apiLogService.save(apiLog);

            log.info(
                    "API操作日志保存成功，traceId：{}，操作：{}，请求：{} {}，耗时：{}ms",
                    apiLog.getTraceId(),
                    apiLog.getTitle(),
                    apiLog.getMethod(),
                    apiLog.getRequestUri(),
                    apiLog.getTime()
            );
        } catch (Exception exception) {
            log.error(
                    "API操作日志保存失败，traceId：{}，操作：{}",
                    apiLog.getTraceId(),
                    apiLog.getTitle(),
                    exception
            );
        }
    }
}
