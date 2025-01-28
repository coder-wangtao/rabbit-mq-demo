package com.wangtao;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import feign.Logger;
import feign.Retryer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients //扫描自己所在包和子包下所有加了@FeignClient注解的接口创建对象添加到容器中
@EnableDiscoveryClient
@EnableCircuitBreaker //启用断路器
@EnableHystrixDashboard  //启用断路器监控面板
public class GuliOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliOrderApplication.class, args);
    }

    //配置重试对象
//    @Bean
//    public Retryer retryer() {
//        //创建重试对象 默认重试次数为5次
//        return Retryer.NEVER_RETRY;
//    }

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

    //springboot项目默认所有的请求都有springmvc的前端控制前来处理
    //配置监控数据流获取数据流视图的servlet
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}