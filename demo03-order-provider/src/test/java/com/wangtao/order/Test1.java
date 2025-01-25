package com.wangtao.order;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class Test1 {
    @Resource
    RabbitTemplate rabbitTemplate;
    @Test
    public void test1(){
        rabbitTemplate.convertAndSend("buss2.exchange","buss2.msg","heehh1....");
    }

    @Test
    public void test2(){
        rabbitTemplate.convertAndSend("business.exchange","stock.business","heehh....");
    }

    @Test
    public void test3(){
        Map map = new HashMap<>();
        map.put("id","1");
        map.put("name","1111");
        rabbitTemplate.convertAndSend("pay.exchange","params.key",map);
    }




}
