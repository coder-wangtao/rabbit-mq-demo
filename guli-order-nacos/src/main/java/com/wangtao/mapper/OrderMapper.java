package com.wangtao.mapper;

import com.wangtao.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMapper extends JpaRepository<OrderEntity,Integer> {
}
