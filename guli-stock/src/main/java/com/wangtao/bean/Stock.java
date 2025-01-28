package com.wangtao.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_stock")
public class Stock  {
    @Id
    private Long id;
    @Column(name = "product_id")
    private Long productId;
    private String title;
    private Integer count;
}
