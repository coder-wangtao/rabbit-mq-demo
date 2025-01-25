package com.wangtao.stock.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockMqConfig {

    //1死信交换机
    @Bean
    public Exchange deadExchange() {
        return ExchangeBuilder.topicExchange("dead.exchange").ignoreDeclarationExceptions().build();
    }
    //2死信队列
    @Bean
    public Queue deadQueue() {
        return QueueBuilder.durable("dead.queue").build();
    }
    //3死信队列绑定死信交换机
    @Bean
    public Binding deadBinding(Exchange deadExchange, Queue deadQueue) {
        return BindingBuilder.bind(deadQueue).to(deadExchange).with("dead.msg").noargs();
    }

    //业务
    //业务交换机
    @Bean
    public Exchange bussinessExchange(Exchange deadExchange, Queue deadQueue) {
        return ExchangeBuilder.topicExchange("business.exchange").ignoreDeclarationExceptions().build();
    }

    //业务队列:业务队列绑定死信交换机
    @Bean
    public Queue businessQueue() {
        //以后business.queue队列丢弃消息时，会通过dead.exchange+dead.msg作为路由key分发消息
        return QueueBuilder.durable("business.queue")
                .deadLetterExchange("dead.exchange")
                .deadLetterRoutingKey("dead.msg").build();
    }

    //业务队列绑定业务交换机
    @Bean
    public Binding businessBinding(Exchange bussinessExchange, Queue businessQueue) {
        return BindingBuilder.bind(businessQueue).to(bussinessExchange).with("stock.business").noargs();
    }
}
