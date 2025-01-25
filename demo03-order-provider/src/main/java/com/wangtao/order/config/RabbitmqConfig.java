package com.wangtao.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

@Configuration
public class RabbitmqConfig implements InitializingBean {
    @Resource
    private RabbitTemplate rabbitTemplate;

    //spring管理bean时提供的生命周期方法 可以在当前类对象属性值初始化后，容器调用afterPropertiesSet方法
    @Override
    public void afterPropertiesSet() throws Exception {
        //消息是否到达交换机的回调
        rabbitTemplate.setConfirmCallback((@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause)-> {
           if(!ack){ //消息没有到达交换机
               //发送消息失败回调中可以尝试重新发下哦那个mq消息
               //但是开发中不会重发：原因1 rabbitmq性能稳定失败概率低 原因2新跟那个差一般保存日志 手动处理
               System.out.println("消息是否到达交换机：" + ack);
               System.out.println(ack ? "消息到达交换机" :("未到达的原因" + cause));
           }
        });


        //消息没有到达队列的回调
        rabbitTemplate.setReturnCallback((Message message, int replayCode, String replyText, String exchange, String routingKey) -> {
            try {
                System.out.println("消息没有到达队列： replayCode=" + replayCode + ", replyText=" + replyText + ", exchange=" + exchange + ", routingKey=" + routingKey + ", message=" + new String(message.getBody(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    //创建交换机对象添加到容器中
    @Bean
    public Exchange payExchange() {
        return ExchangeBuilder.topicExchange("pay.exchange")
                .ignoreDeclarationExceptions() //忽略声明时的异常，避免异常导致程序宕机
                .durable(true) //持久化 默认true
                .build();
    }

    //创建队列
    @Bean
    public Queue payQueue() {
        return QueueBuilder.durable("pay.queue").build();
    }

    //创建绑定对象，将队列绑定到指定的交换机
    @Bean
    public Binding payBinding(Queue payQueue, Exchange payExchange) {
        return BindingBuilder.bind(payQueue).to(payExchange).with("pay.*") //队列绑定时的路由key
                .noargs();
    }
}
