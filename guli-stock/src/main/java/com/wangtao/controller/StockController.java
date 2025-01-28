package com.wangtao.controller;

import com.wangtao.service.StockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/stock")
public class StockController {
    @Value("${server.port}")
    private Integer port;

    @Resource
    StockService stockService;

    //根据商品id 修改商品的库存
    @GetMapping("/updateStockByPId/{productId}/{count}")
    public Boolean updateStockByPId(
            @PathVariable("productId") Long productId,
            @PathVariable("count") Integer count,
            @CookieValue(value = "token",required = false) String token
            ) throws InterruptedException {
//        TimeUnit.SECONDS.sleep(3);
        System.out.println("token:"+token);
        System.out.println("库存服务端口号：" + port + "被访问了。。。");
        return stockService.updateStockByPId(productId,count);
    }
}
