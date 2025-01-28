package com.wangtao.feign.fallback;

import com.wangtao.entity.StockEntity;
import com.wangtao.feign.StockClient;
import org.springframework.stereotype.Component;

//降级方案
@Component
public class StockFallback implements StockClient {

    @Override
    public Boolean updateStock(String productId, Integer count) {
        System.out.println("StockFallback updateStock兜底方法");
        return false;
    }

    @Override
    public String test1(String username, String password) {
        System.out.println("StockFallback test1兜底方法");
        return null;
    }

    @Override
    public String test2(StockEntity stockEntity) {
        System.out.println("StockFallback test2兜底方法");
        return null;
    }

    @Override
    public String test3(StockEntity stockEntity) {
        System.out.println("StockFallback test3兜底方法");
        return null;
    }
}
