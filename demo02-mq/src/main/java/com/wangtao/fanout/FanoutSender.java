package com.wangtao.fanout;

//广播模型
//一个交换机 多个队列保定交换机
//每个消费者消费自己队列中的消息，每个队列的消息都是一样的

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wangtao.utils.ConnectionUtils;

import java.io.IOException;

public class FanoutSender {
    public static void main(String[] args) throws Exception {
        //获取链接
        Connection conn = ConnectionUtils.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //声明交换机
        //参数一交换机名称
        //参数一交换机类型（fanout direct topic）
        //FANOUT 所有绑定到该交换机上的队列 全都能获取到消息
        channel.exchangeDeclare("fanout.exchange", BuiltinExchangeType.FANOUT,false);
        //交换机不存储消息，如果没有队列绑定交换机，消息会丢弃
        //发送消息到交换机中，需要消费消息的消费者自己声明自己的队列绑定当前交换机
        String msg = "hello,fanout...";
        channel.basicPublish("fanout.exchange", "", null, msg.getBytes());

        //关闭通道/连接
        channel.close();
        conn.close();
    }
}
