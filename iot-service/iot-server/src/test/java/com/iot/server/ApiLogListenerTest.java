package com.iot.server;

import com.iot.core.log.config.IotLogAutoConfiguration;
import com.iot.core.log.model.LogApi;
import com.iot.core.log.publisher.ApiLogPublisher;
import com.iot.server.config.IotLogConfiguration;
import com.iot.server.log.listener.ApiLogListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class ApiLogListenerTest {

    @Test
    void receivesPublishedApiLogEvent(CapturedOutput output) {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(IotLogAutoConfiguration.class))
                .withUserConfiguration(IotLogConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ApiLogListener.class);

                    LogApi apiLog = new LogApi();
                    apiLog.setTraceId("trace-listener-test");
                    apiLog.setTitle("查询设备");
                    apiLog.setMethodName("findDevice");
                    apiLog.setTime("18");

                    context.getBean(ApiLogPublisher.class).publish(apiLog);

                    assertThat(output)
                            .contains("接收到API操作日志")
                            .contains("trace-listener-test")
                            .contains("查询设备")
                            .contains("findDevice")
                            .contains("18ms");
                });
    }
}
