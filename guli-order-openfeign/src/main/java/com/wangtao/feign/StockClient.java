package com.wangtao.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "GULI-STOCK")
public interface StockClient {

    @GetMapping("/stock/updateStockByPId/{productId}/{count}")
    public Boolean updateStockByPId(
            @PathVariable Long productId,
            @PathVariable Long count);
}
