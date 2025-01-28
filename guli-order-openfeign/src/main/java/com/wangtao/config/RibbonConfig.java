package com.wangtao.config;

import com.netflix.loadbalancer.RandomRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RibbonConfig {

    //向容器中添加轮询策略对象覆盖默认的
    @Bean
    public RandomRule randomRule() {
        return new RandomRule();  //随机的轮训策略
    }

    @Bean
    @LoadBalanced  //使用ribbon的负载均衡 对RestTemplate进行增强
    //RestTemplate是springweb包提供的 对httpClient封装后 网络请求的工具类
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
 }
