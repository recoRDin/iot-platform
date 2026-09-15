package com.iot.core.redis.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.core.redis.cache.IotRedis;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@AutoConfiguration(before = RedisAutoConfiguration.class)
@ConditionalOnClass({RedisTemplate.class, ObjectMapper.class})
public class IotRedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);


        // Key使用字符串，Value使用JSON
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);

        // Redis Hash内部的字段名和字段值
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }

    @Bean
    @ConditionalOnMissingBean(IotRedis.class)
    public IotRedis iotRedis(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {

        return new IotRedis(redisTemplate, objectMapper);
    }
}
