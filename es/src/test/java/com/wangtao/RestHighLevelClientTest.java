package com.wangtao;

import com.alibaba.fastjson2.JSON;
import com.wangtao.bean.Goods;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class RestHighLevelClientTest {

    @Resource
    RestHighLevelClient client;


    //查询有货的小米手机前5条记录，标题关键字高亮显示，按照价格排序，只要标题价格图片属性，并查询品牌分类桶
    @Test
    void test1() throws IOException {
        //请求参数封装
        SearchRequest searchRequest = new SearchRequest();
        //dsl构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //使用bool组合多个查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("title","小米手机"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("stock").gt(0));
        sourceBuilder.query(boolQueryBuilder);
        //价格排序
        sourceBuilder.sort("price", SortOrder.ASC);
        //分页
        sourceBuilder.from(0);
        sourceBuilder.size(5);

        //高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"));
        //结果过滤
        sourceBuilder.fetchSource(new String[]{"title","id","attr.brand","price"},new String[]{});
        //聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("品牌桶").field("attr.brand"));
        sourceBuilder.aggregation(AggregationBuilders.terms("分类桶").field("attr.category"));
        //输出构建的dsl语句
        System.out.println(sourceBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        //获取总记录数
        System.out.println("文档总数量:"+response.getHits().getTotalHits().value);
        //获取查询到的文档结果集中的文档集合
        SearchHit[] searchHits = response.getHits().getHits();
        List<Goods> result = Arrays.asList(searchHits).stream().map(hit -> {
            //获取文档内容
            String goodsJson = hit.getSourceAsString();
            //获取高亮属性值
            HighlightField highlightField = hit.getHighlightFields().get("title");
            Goods goods = JSON.parseObject(goodsJson, Goods.class);
            goods.setTitle(Arrays.toString(highlightField.getFragments()));
            return goods;
        }).collect(Collectors.toList());
        System.out.println(result);
        //获取最外层的聚合结果集合：桶名-》聚合对象
        Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();
        //强转的目的：使用子类特有的方法
        ParsedStringTerms brandAggs = (ParsedStringTerms) aggregationMap.get("品牌桶");
        brandAggs.getBuckets().forEach(brandAgg -> {
            System.out.println(brandAgg.getKey());
            System.out.println(brandAgg.getDocCount());
        });
        System.out.println("=============================");
        ParsedStringTerms cateAggs = (ParsedStringTerms) aggregationMap.get("分类桶");
        cateAggs.getBuckets().forEach(cateAgg -> {
            System.out.println(cateAgg.getKey());
            System.out.println(cateAgg.getDocCount());
        });
    }
}
