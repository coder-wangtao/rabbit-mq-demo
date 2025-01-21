package com.wangtao.fanout;

import com.rabbitmq.client.*;
import com.wangtao.utils.ConnectionUtils;

import java.io.IOException;

public class FanoutReceiver2 {
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
        //声明队列
        channel.queueDeclare("fanout.queue2", false,false,false,null);
        //队列绑定交换机
        channel.queueBind("fanout.queue2", "fanout.exchange", "");


        //获取队列中的消息：创建消费者监听
        Consumer consumer = new DefaultConsumer(channel) {
            //获取到队列中消息时的回调方法：如果autoAck设置的是true,此方法一旦被调用表示消息被消费，channel会自动确认消息被消费
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //获取消息：执行业务
                System.out.println("FanoutReceiver2获取到消息：" + new String(body));
                //代码执行到当前行，消息消费的业务才算执行成功
                channel.basicAck(envelope.getDeliveryTag(), false); //手动确认消息
                // channel.basicNack(); //不确认消息，让消息重新归队
                // channel.basicReject(); //拒绝消息 可以丢弃消息或者让消息重新归队
            }
        };
        //第二个参数AutoAck表示是否自动ack确认消息被消费（mq） //消息确认必须使用false: 否则能者多劳不生效
        channel.basicConsume("fanout.queue2", false, consumer);

    }
}
