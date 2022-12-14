package com.fairy.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 鹿少年
 * @date 2022/7/31 21:54
 */
@SpringBootApplication(scanBasePackages = {"com.fairy.cloud.gateway"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableScheduling
public class GateWayApp {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApp.class, args);
    }
}
