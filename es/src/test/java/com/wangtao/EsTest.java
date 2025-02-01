package com.wangtao;


import com.wangtao.bean.Attr;
import com.wangtao.bean.Goods;
import com.wangtao.repository.GoodsRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;

import javax.annotation.Resource;
import java.sql.Array;
import java.util.ArrayList;

@SpringBootTest
public class EsTest {
    @Resource
    ElasticsearchRestTemplate restTemplate;

    @Resource
    GoodsRepository goodsRepository;

    @Resource
    RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
        System.out.println("restTemplate"+restTemplate);
        System.out.println("restHighLevelClient"+restHighLevelClient);
        //在es中创建新的索引库

        //给索引库创建映射

        //新增文档到索引库

        //更新文档

        //删除指定文档
    }

    @Test
    public void test1(){
        //基于Goods类生成一个可以操作es数据的对象：目前还没有请求es
        IndexOperations indexOps = restTemplate.indexOps(Goods.class);
        //判断goods对应的索引库是否存在，会请求es
        boolean exists = indexOps.exists();
        System.out.println("exists"+exists);
        if(!exists){
            //索引库不存在：会创建索引库
            indexOps.create();
            //创建映射
            //在内存中创建映射
            Document document = indexOps.createMapping();
            //将映射更新到es的索引库中
            indexOps.putMapping(document);
        }
    }
    //EsRepository测试CURD
    //EsRepository新增
    @Test
    public void test2(){
        ArrayList<Goods> goodsList = new ArrayList<>();
        Goods goods1 = new Goods(1L, "小米手机11promax", "http://xiaomi.jpg", 1999D, 200, new Attr("小米", "手机"));
        goodsList.add(goods1);
        goods1 = new Goods(2L, "小米手机12promax", "http://xiaomi12.jpg", 2999D, 201, new Attr("小米", "手机"));
        goodsList.add(goods1);
        goods1 = new Goods(3L, "apple手机promax", "http://ipone14promax.jpg", 5999D, 10, new Attr("apple", "手机"));
        goodsList.add(goods1);
        goods1 = new Goods(4L, "小米电视6pro", "http://xioaomi6pro.jpg", 19999D, 200, new Attr("小米", "电视"));
        goodsList.add(goods1);
        goods1 = new Goods(5L, "小米游戏本", "http://xioaomibijiben.jpg", 10000D, 10, new Attr("小米", "笔记本"));
        goodsList.add(goods1);
        goodsList.add(goods1);
        goods1 = new Goods(6L, "macbook", "http://macbook.jpg", 4999D, 100, new Attr("apple", "笔记本"));
        goodsList.add(goods1);
        goodsRepository.saveAll(goodsList);
    }

    //EsRepository先查询后修改
    @Test
    public void test3(){
        Goods goods = goodsRepository.findById(2L).get();
        System.out.println("id为2的goods:"+goods);
        goods.setPrice(3199D);
        Attr attr = goods.getAttr();
        attr.setBrand("大米");
        goods.setAttr(attr);
        //save：如果对象包含id,会覆盖之前同id的文档
        goodsRepository.save(goods);
    }

    //查询所有文档
    @Test
    public void test4(){
        Iterable<Goods> goodsIterable = goodsRepository.findAll(Sort.by("price").ascending());
        goodsIterable.forEach(System.out::println);
    }

    //删除文档
    @Test
    public void test5(){
        goodsRepository.deleteById(1L);
    }
    //复杂查询：查询价格在2999-3999的文档
    @Test
    public void test6(){
//        Iterable<Goods> goodsIterable = goodsRepository.findByPriceBetween(2999D,3999D);
        Iterable<Goods> goodsIterable = goodsRepository.findByPriceBetween2(2999D,3999D);
        goodsIterable.forEach(System.out::println);
    }
}


