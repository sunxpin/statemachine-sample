package com.baoxian.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient(Environment environment) {
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useSingleServer()
                .setAddress(environment.getProperty("spring.redis.host"))
                .setPassword(environment.getProperty("spring.redis.password"));
        return Redisson.create(config);
    }

}
