package com.iot.core.log.publisher;


import com.iot.core.log.constant.EventConstants;
import com.iot.core.log.event.ApiLogEvent;
import com.iot.core.log.model.LogApi;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//API操作日志发布器
public class ApiLogPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public ApiLogPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(LogApi logApi) {
        Objects.requireNonNull(logApi, "API日志不能为空");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventConstants.EVENT_LOG, logApi);

        eventPublisher.publishEvent(new ApiLogEvent(eventData));
    }
}
