package com.fairy.cloud.gateway.config;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.fairy.cloud.gateway.handler.MyUrlBlockHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huanglulu
 * @version 1.0
 * @createTime 2022/4/4 15:11
 */
@Configuration
public class SentinelConfig {
    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/*");
        // 入口资源关闭聚合  解决流控链路不生效的问题
        registration.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY, "false");
        registration.setName("sentinelFilter");
        registration.setOrder(1);

        //CommonFilter的BlockException自定义处理逻辑
        WebCallbackManager.setUrlBlockHandler(new MyUrlBlockHandler());

//        WebCallbackManager.setUrlCleaner(new MyUrlClean());
        //解决授权规则不生效的问题
//        com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser
//        WebCallbackManager.setRequestOriginParser(new MyRequestOriginParser())

        return registration;
    }
}
