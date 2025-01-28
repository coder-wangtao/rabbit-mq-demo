package com.wangtao.bean;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //数据库自增长
    private Long id;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "order_sn")
    private String orderSn;
    private Long count;
    private BigDecimal money;
    @Column(name = "create_time")
    private Date createTime;

}

