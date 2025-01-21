package com.wangtao.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wangtao.utils.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//simple消息模型生产者
//多个消费者消费同一个队列中的消息，每个消息只能消费一次
//作用：避免消息积压
//默认采用轮询方式将消息平均分发给多个消费者，如果某个消费者性能较差，也会导致消息积压
//问题？
//消息避免丢失？
//生产者发送消息导致丢失
//mq宕机导致消息丢失 默认mq自定义的交换机队列 消息没有持久化 mq非法关闭时，数据都会丢失，声明队列交换机 发送消息时都配置持久化
//消费者消费导致消费丢失：手动ack
//消息积压
//多个消费者消费同一个队列消息+能者多劳

public class WorkSender {
    public static void main(String[] args) throws Exception {
        //获取链接
        Connection conn = ConnectionUtils.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //声明队列，队列可以缓存数据，mq的交换机不存数据  work模型使用的时默认的交换机
        channel.queueDeclare("work.queue", false, false, false, null);


        //发送消息，发送消息到队列
        for (int i = 1; i <= 100; i++) {
            TimeUnit.MICROSECONDS.sleep(i*5);
            channel.basicPublish("", "work.queue", null, ("work.msg:" + i).getBytes());
        }
        //关闭通道/连接
        channel.close();
        conn.close();
    }
}
