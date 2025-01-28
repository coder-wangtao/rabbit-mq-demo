package com.wangtao.controller;

import com.wangtao.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/create/{userId}/{productId}/{count}")
    public String create(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @PathVariable Long count) {
        return orderService.saveOrder(userId,productId,count) ? "订单创建成功" : "订单创建失败";
    }

}
