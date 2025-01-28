package com.wangtao.service;

import com.wangtao.mapper.StockMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StockService {

    @Resource
    StockMapper stockMapper;

    public Boolean updateStockByPId(Long productId, Integer count) {
       int i = stockMapper.updateCountByPId(productId,count);
       return i == 1;
    }
}
