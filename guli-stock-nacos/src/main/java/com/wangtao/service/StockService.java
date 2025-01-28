package com.wangtao.service;

import com.wangtao.entity.StockEntity;
import com.wangtao.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService  {
    @Autowired
    StockMapper stockMapper;
    //根据商品id查询库存的方法
    public StockEntity queryStockEntityByProductId(String productId){
        return stockMapper.findByProductId(productId);
    }
    //更新商品id更新库存
    public Boolean updateStockByProductId(String productId,Integer count){
        return stockMapper.updateCountByProductId(productId,count)==1;
    }
}
