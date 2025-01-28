package com.wangtao.service;

import com.wangtao.entity.OrderEntity;
import com.wangtao.feign.StockClient;
import com.wangtao.mapper.OrderMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Resource
    StockClient stockClient;

    //创建订单
    public boolean saveOrder(String userId,String productId,Integer count){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCount(count);
        orderEntity.setOrderSn(UUID.randomUUID().toString().replace("-",""));
        orderEntity.setCreateTime(new Date());
        orderEntity.setMoney(new BigDecimal(1000));
        orderEntity.setProductId(productId);
        orderEntity.setUserId(userId);
        orderEntity = orderMapper.save(orderEntity);
        System.out.println(orderEntity);
        //TODO: 更新库存
        Boolean aBoolean = stockClient.updateStock(productId,count);
        System.out.println("更新库存：" + aBoolean);
        return orderEntity!=null;

    }

}
