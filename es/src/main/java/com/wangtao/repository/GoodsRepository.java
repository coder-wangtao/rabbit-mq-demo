package com.wangtao.repository;

import com.wangtao.bean.Goods;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
    List<Goods> findByPriceBetween(Double price1,Double price2);

    @Query(value = "{\n" +
            "    \"range\": {\n" +
            "      \"price\": {\n" +
            "        \"gte\": ?0,\n" +
            "        \"lte\": ?1\n" +
            "      }\n" +
            "    }\n" +
            "  }")
    List<Goods> findByPriceBetween2(Double price1,Double price2);

}
