package com.iot.core.log.event;


import org.springframework.context.ApplicationEvent;

import java.util.Map;

//Api操作日志事件
public class ApiLogEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public ApiLogEvent(Map<String, Object> source) {
        super(source);
    }
}
