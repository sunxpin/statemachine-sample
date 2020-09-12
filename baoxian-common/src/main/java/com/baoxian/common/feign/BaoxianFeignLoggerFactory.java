package com.baoxian.common.feign;

import feign.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignLoggerFactory;

/**
 * @author 李德英
 */
public class BaoxianFeignLoggerFactory implements FeignLoggerFactory {
    @Override
    public Logger create(Class<?> type) {
        return new BaoxianFeignLogger(LoggerFactory.getLogger(type));
    }
}
