package com.wangtao.mapper;

import com.wangtao.bean.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;

public interface StockMapper extends JpaRepository<Stock,Long> {
    @Transactional  //JAP的更新必须加事务
    @Modifying //标识本次操作是更新
    @Query("update Stock s set s.count = s.count - ?2 where s.productId = ?1")
    int updateCountByPId(Long productId, Integer count);
}
