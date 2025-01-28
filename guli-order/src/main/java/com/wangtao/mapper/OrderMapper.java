package com.wangtao.mapper;

import com.wangtao.bean.Order;
import org.apache.catalina.mapper.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMapper extends JpaRepository<Order,Long> {

}
