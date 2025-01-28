package com.wangtao.feign;

import com.wangtao.entity.StockEntity;
import com.wangtao.feign.fallback.StockFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

//value:要远程访问服务在注册中心的名称
//url:代表要远程访问的服务名获取配置失败时 要使用的默认的url
//fallback：熔断降级方案对应的类
@FeignClient(value = "stock-nacos-service",fallback = StockFallback.class)
public interface StockClient {

    @GetMapping("/stock/{productId}/{count}")
    public Boolean updateStock(@PathVariable("productId")String productId ,
                               @PathVariable("count") Integer count);

    //多个参数要带RequestParam
    @GetMapping("/stock/test1")
    public String test1(@RequestParam("username") String username, @RequestParam("password") String password);

    @PostMapping("/stock/test2")
    public String test2(@RequestBody StockEntity stockEntity);

    @GetMapping("/stock/test3")
    public String test3(@SpringQueryMap StockEntity stockEntity);
}
