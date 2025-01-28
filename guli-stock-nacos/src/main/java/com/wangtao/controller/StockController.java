package com.wangtao.controller;

import com.wangtao.entity.StockEntity;
import com.wangtao.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockController {
    @Autowired
    StockService stockService;
    @GetMapping("{productId}/{count}")
    public Boolean updateStock(@PathVariable("productId")String productId ,
                               @PathVariable("count") Integer count){
        return stockService.updateStockByProductId(productId,count);
    }

    @GetMapping("test1")
    public String test1(String username,String password){
        System.out.println("test1：" + username + "," + password);
        return "success";
    }

    @PostMapping("test2")
    public String test2(@RequestBody StockEntity stockEntity){
        System.out.println("test2：" + stockEntity);
        return "success";
    }

    @GetMapping("test3")
    public String test3(StockEntity stockEntity){
        System.out.println("test3：" + stockEntity);
        return "success";
    }





}
