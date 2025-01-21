package com.wangtao.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wangtao.utils.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//simple简单消息模型生产者
public class SimpleSender {
    public static void main(String[] args) throws IOException, TimeoutException {
        //获取链接
        Connection conn = ConnectionUtils.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //声明队列，队列可以缓存数据，mq的交换机不存数据 简单模型使用的时默认的交换机
        channel.queueDeclare("simple.queue", false, false, false, null);
        //发送消息，发送消息到队列
        String msg = "hello,simple";
        channel.basicPublish("", "simple.queue", null, msg.getBytes());
        //关闭通道/连接
        channel.close();
        conn.close();
    }

}
