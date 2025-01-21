package com.wangtao;


import com.rabbitmq.client.Connection;
import com.wangtao.utils.ConnectionUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestDemo {

    @Test
    public void contextLoads() {
        Connection conn = ConnectionUtils.getConn();
        System.out.println(conn);

    }
}
