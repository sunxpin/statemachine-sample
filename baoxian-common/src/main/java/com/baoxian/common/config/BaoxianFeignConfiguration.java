package com.baoxian.common.config;

import com.baoxian.common.feign.BaoxianFeignLoggerFactory;
import feign.Logger;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 李德英
 */
@Configuration
public class BaoxianFeignConfiguration {
    @Bean
    Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    FeignLoggerFactory infoFeignLoggerFactory() {
        return new BaoxianFeignLoggerFactory();
    }
}
