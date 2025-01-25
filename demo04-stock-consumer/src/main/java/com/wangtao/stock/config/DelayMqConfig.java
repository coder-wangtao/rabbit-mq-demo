package com.wangtao.stock.config;

import org.junit.Test;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelayMqConfig {
    //延迟队列
    //延迟对列：死信队列

    //业务队列：设置过期时间 并且绑定死信交换机
    @Bean
    public Exchange delayExchange() {
        return ExchangeBuilder.topicExchange("delay.exchange").ignoreDeclarationExceptions().build();
    }

    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable("delay.queue").build();
    }

    //业务队列：设置过期时间 并且绑定死信交换机
    @Bean
    public Binding delayBinding(Exchange delayExchange, Queue delayQueue) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with("delay.msg").noargs();
    }

    @Bean
    public Exchange buss2Exchange() {
        return ExchangeBuilder.topicExchange("buss2.exchange").ignoreDeclarationExceptions().build();
    }

    @Bean
    public Queue buss2Queue() {
        return QueueBuilder.durable("buss2.queue")
                //配置延迟交换机路由key
                //配置过期时间
                //当消息到达过期时间没有消费时会自动丢弃到延迟交换机中
                .ttl(1*60*1000)
                .deadLetterExchange("delay.exchange")
                .deadLetterRoutingKey("delay.msg")
                .build();
    }

    @Bean
    public Binding buss2Binding(Exchange buss2Exchange, Queue buss2Queue) {
        return BindingBuilder.bind(buss2Queue).to(buss2Exchange).with("buss2.msg").noargs();
    }
}
