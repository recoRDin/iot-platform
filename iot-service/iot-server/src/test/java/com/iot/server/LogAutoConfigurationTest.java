package com.iot.server;

import com.iot.core.log.config.IotLogAutoConfiguration;
import com.iot.core.log.error.RestExceptionTranslator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class LogAutoConfigurationTest {

    @Test
    void doesNotRegisterMvcAdviceInNonWebApplications() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(IotLogAutoConfiguration.class))
                .run(context -> assertThat(context).doesNotHaveBean(RestExceptionTranslator.class));
    }

    @Test
    void backsOffWhenApplicationSuppliesItsOwnTranslator() {
        RestExceptionTranslator customTranslator = new RestExceptionTranslator();
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(IotLogAutoConfiguration.class))
                .withBean("customTranslator", RestExceptionTranslator.class, () -> customTranslator)
                .run(context -> {
                    assertThat(context).hasSingleBean(RestExceptionTranslator.class);
                    assertThat(context.getBean(RestExceptionTranslator.class)).isSameAs(customTranslator);
                });
    }
}
