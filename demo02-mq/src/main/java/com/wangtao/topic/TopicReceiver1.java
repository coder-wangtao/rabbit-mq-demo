package com.wangtao.topic;

import com.rabbitmq.client.*;
import com.wangtao.utils.ConnectionUtils;

import java.io.IOException;

public class TopicReceiver1 {
    public static void main(String[] args) throws Exception {
        //获取链接
        Connection conn = ConnectionUtils.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //声明交换机
        //参数一交换机名称
        //参数二交换机类型（fanout direct topic）
        //FANOUT 所有绑定到该交换机上的队列 全都能获取到消息
        //DIRECT 定向指定队列，路由key是固定的（order.save）
        //TOPIC  定向指定队列，路由key是固定的（a.#）
        //参数三交换机是否持久化
        channel.exchangeDeclare("topic.exchange", BuiltinExchangeType.TOPIC,true);
        //声明队列,第二个参数队列是否持久化
        channel.queueDeclare("topic.queue1", true,false,false,null);
        //队列绑定交换机,需要指定路由key(TOPIC,可以匹配)
        //#任意多级多字符 *通配一级任意多个字符
        channel.queueBind("topic.queue1", "topic.exchange", "a.#",null);

        //获取队列中的消息：创建消费者监听
        Consumer consumer = new DefaultConsumer(channel) {
            //获取到队列中消息时的回调方法：如果autoAck设置的是true,此方法一旦被调用表示消息被消费，channel会自动确认消息被消费
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //获取消息：执行业务
                System.out.println("TopicReceiver1获取的消息：" + new String(body));
                //代码执行到当前行，消息消费的业务才算执行成功
                channel.basicAck(envelope.getDeliveryTag(), false); //手动确认消息
                // channel.basicNack(); //不确认消息，让消息重新归队
                // channel.basicReject(); //拒绝消息 可以丢弃消息或者让消息重新归队
            }
        };
        //第二个参数AutoAck表示是否自动ack确认消息被消费（mq） //消息确认必须使用false: 否则能者多劳不生效
        channel.basicConsume("topic.queue1", false, consumer);
    }
}
