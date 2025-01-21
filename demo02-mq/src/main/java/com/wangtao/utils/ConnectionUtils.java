package com.wangtao.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


//生产者：发送消息
//消费者：消费消息

//五大消费模型
//1.simple消息模型
//一个生产者 一个消费者 一个队列
//2.work消费模型
//一个生产者 多个消费者 一个队列 //能者多劳 消费者性能高的可以多消费消息
//3.fanout消费模型
//一个生产者 一个交换机 多个队列 多个消费者
//4.direct消费模型
//一个生产者 一个交换机 多个队列 多个消费者 (队列绑定交换机时需要指定routingkey)
//5.topic消费模型：开发中使用最多
//一个生产者 一个交换机 多个队列 多个消费者 (队列绑定交换机时使用routingkey，可以使用通配符，*统配一级任意多个字符，#匹配任意多级任意多个字符)

public class ConnectionUtils {
    private static ConnectionFactory factor;
    static{
        factor = new ConnectionFactory();
        factor.setHost("localhost");
        factor.setPort(5672);
        factor.setVirtualHost("/sh221109");
        factor.setUsername("fangfang");
        factor.setPassword("123456");
    }

    public static Connection getConn() {
        try {
            return factor.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
