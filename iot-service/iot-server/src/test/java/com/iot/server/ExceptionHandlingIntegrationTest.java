package com.iot.server;

import com.iot.core.log.error.RestExceptionTranslator;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tool.api.IResultCode;
import com.iot.core.tool.api.R;
import com.iot.core.tool.api.ResultCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 仅导入测试接口，异常处理器必须由 starter 的自动配置发现。
 */
@SpringBootTest(classes = IotServerApplication.class)
@AutoConfigureMockMvc
@Import(ExceptionHandlingIntegrationTest.TestController.class)
class ExceptionHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @Test
    void registersExactlyOneTranslatorAutomatically() {
        assertThat(context.getBeansOfType(RestExceptionTranslator.class)).hasSize(1);
    }

    @Test
    void preservesCustomBusinessMessage() throws Exception {
        mockMvc.perform(get("/test/exception/message"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.msg").value("产品编码已存在"));
    }

    @Test
    void usesDefaultResultMessage() throws Exception {
        mockMvc.perform(get("/test/exception/default"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("业务异常"));
    }

    @Test
    void keepsBusinessCodeSeparateFromHttpStatus() throws Exception {
        mockMvc.perform(get("/test/exception/custom-code"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.msg").value("产品不存在"));
    }

    @Test
    void successfulRequestsStillReturnTheirData() throws Exception {
        mockMvc.perform(get("/test/exception/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Hello IoT Learning"));
    }

    /**
     * 测试接口只存在于测试源码中，不会进入正式服务包。
     */
    @RestController
    static class TestController {

        @GetMapping("/test/exception/message")
        public R<Void> customMessage() {
            throw new ServiceException("产品编码已存在");
        }

        @GetMapping("/test/exception/default")
        public R<Void> defaultMessage() {
            throw new ServiceException(ResultCode.FAILURE);
        }

        @GetMapping("/test/exception/custom-code")
        public R<Void> customCode() {
            throw new ServiceException(new IResultCode() {
                @Override
                public int getCode() {
                    return 10001;
                }

                @Override
                public String getMessage() {
                    return "产品不存在";
                }
            });
        }

        @GetMapping("/test/exception/success")
        public R<String> success() {
            return R.data("Hello IoT Learning");
        }
    }
}
