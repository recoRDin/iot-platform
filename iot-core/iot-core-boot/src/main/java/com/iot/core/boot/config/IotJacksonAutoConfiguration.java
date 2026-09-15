package com.iot.core.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

@AutoConfiguration(before = JacksonAutoConfiguration.class)
@ConditionalOnClass(ObjectMapper.class)
public class IotJacksonAutoConfiguration {


    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer iotJacksonCustomizer() {

        return builder -> {

            // Java 时间对象转换成 JSON 字符串
            builder.serializers(
                    new LocalDateTimeSerializer(DATE_TIME_FORMATTER),
                    new LocalDateSerializer(DATE_FORMATTER),
                    new LocalTimeSerializer(TIME_FORMATTER)
            );

            // JSON 字符串转换成 Java 时间对象
            builder.deserializers(
                    new LocalDateTimeDeserializer(DATE_TIME_FORMATTER),
                    new LocalDateDeserializer(DATE_FORMATTER),
                    new LocalTimeDeserializer(TIME_FORMATTER)
            );

            // 日期时间使用文本形式输出，不使用时间戳或数组形式
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
