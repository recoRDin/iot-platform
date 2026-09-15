package com.iot.server.log.controller;

import com.iot.core.log.annotation.ApiLog;
import com.iot.core.tool.api.R;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/dev/api-log")
public class ApiLogDemoController {

    @ApiLog("验证API日志持久化")
    @GetMapping
    public R<String> createApiLog() {
        return R.data("API日志事件已发布");
    }
}