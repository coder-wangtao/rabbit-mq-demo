package com.wangtao.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
public class StockEntity {
    private Integer id;
    private String productId;
    private String title;
    private Integer count;
}

