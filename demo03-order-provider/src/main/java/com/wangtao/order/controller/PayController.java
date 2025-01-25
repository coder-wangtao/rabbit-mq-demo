package com.wangtao.order.controller;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Resource
    RabbitTemplate rabbitTemplate;

    //生产者发送消息失败消息丢失：
            //消息发送失败时：没有发送到mq的交换机（交换机不存在、网络通信失败）
            //交换机没有绑定队列（交换机会丢弃消息）
    //生产者确认解决：
            //rabbitTemplate 可以设置生产者确认回调 消息是否到达交换机的回调 消息没有到达队列的回调
    //mq宕机消息丢失：持久化（交换机、消息、队列）
    //消费者异常导致消息丢失：手动ack

    @GetMapping("{orderToken}")
    public String pay(@PathVariable("orderToken") String orderToken) {
        System.out.println("订单编号" + orderToken + "支付成功");
        //交换机需要提前创建

        //订单支付成功：发送消息到mq交换机
        rabbitTemplate.convertAndSend("pay.exchange", "pay.success", orderToken);
        return "success";
    }


}
