package com.wangtao.work;

import com.rabbitmq.client.*;
import com.wangtao.utils.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WorkReceiver2 {
    public static void main(String[] args) throws Exception {
        //获取链接
        Connection conn = ConnectionUtils.getConn();
        //打开通道
        Channel channel = conn.createChannel();
        //声明队列（如果已声明可以忽略）
        channel.queueDeclare("work.queue", false, false, false, null);

        //配置能者多劳，指定一次获取几条消息，消息消费成功后 ack之后mq才能分发下一条信息
        channel.basicQos(1);

        //获取队列中的消息：创建消费者监听
        Consumer consumer = new DefaultConsumer(channel) {
            //获取到队列中消息时的回调方法：如果autoAck设置的是true,此方法一旦被调用表示消息被消费，channel会自动确认消息被消费
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    //模拟业务执行较慢
                    TimeUnit.MILLISECONDS.sleep(2000);
                    //获取消息：执行业务
                    System.out.println("WorkReceiver2获取到消息：" + new String(body));
                    //代码执行到当前行，消息消费的业务才算执行成功
                    channel.basicAck(envelope.getDeliveryTag(), false); //手动确认消息
                    // channel.basicNack(); //不确认消息，让消息重新归队
                    // channel.basicReject(); //拒绝消息 可以丢弃消息或者让消息重新归队
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        //第二个参数AutoAck表示是否自动ack确认消息被消费（mq） //消息确认必须使用false: 否则能者多劳不生效
        channel.basicConsume("work.queue", false, consumer);
    }
}
