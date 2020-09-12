package com.baoxian.common.feign;

import com.baoxian.common.annotation.LoggerRequest;
import com.baoxian.common.util.FeignUtil;
import feign.Request;
import feign.Response;
import feign.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

/**
 * @author 李德英
 */
public class BaoxianFeignLogger extends feign.Logger {

    private final Logger logger;

    public BaoxianFeignLogger() {
        this(feign.Logger.class);
    }

    public BaoxianFeignLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public BaoxianFeignLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    BaoxianFeignLogger(Logger logger) {
        this.logger = logger;
    }


    @SuppressWarnings("deprecation")
	@Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        LoggerRequest loggerRequest = FeignUtil.get(configKey);

        if (loggerRequest == null || loggerRequest.urlLogger()) {
            log(configKey, "请求地址: %s %s", request.httpMethod().name(), request.url());
        }
        if (loggerRequest == null || loggerRequest.bodyLogger()) {
            String bodyText = request.charset() != null
                    ? new String(request.requestBody().asBytes(), request.charset())
                    : null;
            log(configKey, "请求参数: %s", bodyText != null ? bodyText.replaceAll("[\r\n\t]", "") : "Binary data");
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
            throws IOException {
        LoggerRequest loggerRequest = FeignUtil.get(configKey);

        if (loggerRequest == null || loggerRequest.returnLogger()) {
            int status = response.status();
            if (response.body() != null && !(status == 204 || status == 205)) {
                byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                log(configKey, "返回结果: %s", decodeOrDefault(bodyData, UTF_8, "Binary data").replaceAll("[\r\n\t]", ""));
                return response.toBuilder().body(bodyData).build();
            }
        }
        return response;
    }

    protected IOException logIOException(String configKey, Level logLevel, IOException ioe, long elapsedTime) {
        error(configKey, ioe, "%s: %s (%sms)", ioe.getClass().getName(), ioe.getMessage(), elapsedTime);
        return ioe;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        logger.info(String.format(methodTag(configKey) + format, args));
    }

    protected void error(String configKey, IOException ioe, String format, Object... args) {
        logger.error(String.format(methodTag(configKey) + format, args), ioe);
    }
}
