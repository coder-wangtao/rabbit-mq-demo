package com.wangtao.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.wangtao.bean.Order;
import com.wangtao.feign.StockClient;
import com.wangtao.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {
     @Resource
     OrderMapper orderMapper;

     @Resource
     StockClient stockClient;

     @HystrixCommand(fallbackMethod = "saveOrderFallback") //异常兜底方案
    public Boolean saveOrder(Long userId, Long productId,Long count) {
        //根据传入的阐述创建order对象保存到数据库
        Order order = new Order();
        order.setCount(count);
        order.setCreateTime(new Date());
        order.setOrderSn(UUID.randomUUID().toString().replace("-",""));
        order.setProductId(productId);
        order.setUserId(userId);
        order.setMoney(new BigDecimal(1000*count));
        boolean flag = orderMapper.save(order) != null;
        //todo 更新库存
        //第一种（不行）
//        try {
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            String path = "http://localhost:8081/stock/updateStockByPId/%s/%s";
//            path = String.format(path,productId,count);
//            HttpGet request = new HttpGet(path);
//            HttpResponse response = httpClient.execute(request);
//            String s = EntityUtils.toString(response.getEntity());
//            flag = Boolean.parseBoolean(s);
//            System.out.println("扣除库存结果：" + s);
//        } catch (Exception e) {
//            e.printStackTrace();
//            flag = false;
//        }
        //参数1： 目标接口地址
        //参数2：目标接口返回的响应体数据要封装的类型
        //参数3：可变参数列表 填充参数1中的占位符
//        flag = restTemplate.getForObject("http://GULI-STOCK/stock/updateStockByPId/{1}/{2}", Boolean.class, productId, count);
        flag = stockClient.updateStockByPId(productId, count);
        return flag;
    }

    //saveOrder的兜底方法：如果saveOrder执行没有异常 兜底方法不会执行，一旦saveOrder执行出错执行该方法
    public Boolean saveOrderFallback(Long userId, Long productId,Long count) {
        log.error("保存订单失败，进入熔断降级方法，参数列表：userId={},"+"productId={},"+"count={}",userId,productId,count);
        return false;
    }
}
