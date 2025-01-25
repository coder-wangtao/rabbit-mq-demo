package com.wangtao.stock.listener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.ChannelN;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class StockListener {

    //监听订单支付成功的消息
    //RabbitListener标注的方法就是一个消费者方法，可以配置要消费消息的队列
    @RabbitListener(
            //如果消费的队列其他地方已经创建，可以直接使用队列名称绑定队列进行监听消费
            queues ={"pay.queue"}
    )
    public void test(String orderToken,Message message,Channel channel) {
        try {
            System.out.println("stock服务接收到消息：" + orderToken);
            //手动ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("手动ack出现异常");
        }

    }

    //库存服务自定义队列绑定到订单服务的交换机
    //springboot整合mq:
    //消费者默认消息的确认方式
    //出现异常时消息会重新发会队列中，消费者再次尝试消费
    //如果一直出现异常，会出现死循环消费
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "stock.pay.queue",declare = "true"), //创建队列
                    //@Exchange表示创建交换机：如果交换机已存在，旧创建的交换机必须和之前的配置一样
                    exchange = @Exchange(name = "pay.exchange",type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true"),
                    key = "pay.#"  //路由key
            )
    )
    //参数1： 接受队列中的消息  参数2: 消息对象
    void paySuccess(String orderToken, Message message,Channel channel) throws IOException {
        try {
            System.out.println("库存服务接收到订单创建成功的消息" + orderToken);
            //手动ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.out.println("手动ack出现异常");
            Boolean flag = message.getMessageProperties().isRedelivered(); //获取消息是否是重新投递的
            if (flag) {
                //消息时重新投递后再次消费出现异常被捕获 丢弃消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
            //消息第一次消费失败，被捕获，最后一个参数表示重新投递
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }

        //手动ack时：
        //业务方法可能会出现异常，导致消息不能正确ack一直处于unacked状态，直到消费者进程停止
        //如果是暂时的网络异常导致本次消费异常，过一会网络正常就可以正确消费消息了，但是消息不会重新回到队列中
        //为了避免消费失败的消息一直不能被处理处于unacked状态
        //解决：
        //1手动ack时有编译时的异常，trycatch时可以捕获最大的异常
        //2当第一次消费失败时进入到了catch中
        //可以让消息重新回到队列中（状态从unacked转成ready），让消费者再次尝试消费
        //3当第二次消息消费失败时进入到了catch中
        //可以丢弃消息（死信队列：丢弃消息的队列如果绑定了死信队列，消息达到死信队列，如果没有绑定消息丢失）
        //如果消息无关紧要：可以丢弃
        //重要消息：
        //1 生产者确认
        //2 交换机队列消息持久化
        //3 消费者手动ack
        //手动ack时 消息重复消费失败会被丢弃，可以给改队列绑定死信队列
        //死信队列：本质上是一个队列
        //一般会为私信队列创建 交换机 队列 特定路由哦key绑定
        //一般会为死信队列创建 交换机 队列 以及特定的路由key绑定
        //需要使用死信队列的队列，设置额外的参数（死信队列参数，丢弃消息时保存消息的死信配置）


        //队列可以配置的参数在 mq管理控制台，新创建queue时可以看到队列可以配置的参数列表
        //Max length 队列最多可以存储的消息个数
        //Max length bytes 队列所有消息的总字节数限制
        //以上两个配置 哪个先到达阈值哪个先生效
        //Dead letter exchange 死信交换机 队列消息丢弃时由该交换机处理
        // Dead letter routing key 死信路由key 队列消息丢弃时，死信交换机会将消息使用该路由key分发
        //Message TTL 消息的过期时间 单位毫秒
        //如果队列的消息达到设置的过期时间仍然未被消费，消息会被丢弃

        //以后队列的消息丢弃时的情况：
        //1.代码中手动丢弃消息 channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        //2.消息队列已存满不下新的消息时会被丢弃
        //3.消息队列设置了过期时间
    }




    //业务队列的监听器：消费者
    @RabbitListener(
            queues = "business.queue"
    )
    public void business(String msg, Message message,Channel channel) throws IOException {
        try {
            //消费消息的业务代码
            int i = 1/0;
            System.out.println("接收到business队列中的消息：" + msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            Boolean flag = message.getMessageProperties().isRedelivered(); //获取消息是否是重新投递的
            if (flag) {
                //消息时重新投递后再次消费出现异常被捕获 丢弃消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
            //消息第一次消费失败，被捕获，最后一个参数表示重新投递
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    //延迟队列的监听器
    @RabbitListener(queues = "delay.queue")
    public void delay(Message message,Channel channel,String msg) throws IOException {
        try {
            //消费消息的业务代码
            System.out.println("接收到延迟队列中的消息：" + msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            Boolean flag = message.getMessageProperties().isRedelivered(); //获取消息是否是重新投递的
            if (flag) {
                //消息时重新投递后再次消费出现异常被捕获 丢弃消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }
            //消息第一次消费失败，被捕获，最后一个参数表示重新投递
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                value = @Queue("params.queue"),
                exchange = @Exchange(name = "pay.exchange",type=ExchangeTypes.TOPIC),
                key = "params.key"
            )
    })
    public void params(Map map, Message message, Channel channel) throws IOException {
        System.out.println(map);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
