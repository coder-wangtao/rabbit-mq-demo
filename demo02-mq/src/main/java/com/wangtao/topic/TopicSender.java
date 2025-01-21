package com.wangtao.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.wangtao.utils.ConnectionUtils;

import java.io.IOException;

public class TopicSender {
    public static void main(String[] args) throws Exception {
        //获取链接
        Connection conn = ConnectionUtils.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //声明交换机
        //参数一交换机名称
        //参数二交换机类型（fanout direct topic）
        //FANOUT 所有绑定到该交换机上的队列 全都能获取到消息
        //TOPIC 定向指定队列，发送消息key是固定的（a.b.c）
        //参数三交换机是否持久化
        channel.exchangeDeclare("topic.exchange", BuiltinExchangeType.TOPIC,true);
        //交换机不存储消息，如果没有队列绑定交换机，消息会丢弃
        //发送消息到交换机中，需要消费消息的消费者自己声明自己的队列绑定当前交换机，发送消息的路由key写死
        //MessageProperties.PERSISTENT_TEXT_PLAIN 配置消息文本持久化
        channel.basicPublish("topic.exchange", "a.b.c", MessageProperties.PERSISTENT_TEXT_PLAIN, "{orderId:1001}".getBytes());

        //关闭通道/连接
        channel.close();
        conn.close();
    }
}
