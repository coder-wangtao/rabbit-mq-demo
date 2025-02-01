package com.wangtao;

import com.wangtao.bean.Goods;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class EsTemplateTest {
    @Resource
    ElasticsearchRestTemplate restTemplate;

    //简单增删改查 使用esRepository
    //复杂查询 使用ElasticsearchRestTemplate

    @Test
    void test1(){
        //dsl语句构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //使用构建器可以设置dsl的查询语句
        //queryBuilder.withQuery()  //添加查询条件
        //1.匹配查询
        //queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米"));
        //2.词条查询
        //queryBuilder.withQuery(QueryBuilders.termsQuery("price","3199"));
        //3.模糊查询
        //queryBuilder.withQuery(QueryBuilders.fuzzyQuery("title","apple").fuzziness(Fuzziness.TWO)); fuzziness偏移量
        //4.范围查询
        //queryBuilder.withQuery(QueryBuilders.rangeQuery("price").gte(1999).lte(3999));
        //5.布尔组合查询：查询标题包含小米的价格在1999到3999之间的
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.matchQuery("title","小米"));
//        boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(1999).lte(3999));
        //filter是对must查询后的结果集进行过滤 filter不会影响评分
//        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(1999).lte(3999));
//        queryBuilder.withQuery(boolQueryBuilder);
        //6.查询所有
        queryBuilder.withQuery(QueryBuilders.matchAllQuery());
        //queryBuilder.withSort()  添加排序配置
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        //queryBuilder.withPageable() 添加分页配置
        queryBuilder.withPageable(PageRequest.of(0, 3));
        //queryBuilder.withFields() 结果字段过滤的属性列表
        queryBuilder.withFields("id","price","images","title","attr.brand","attr.category");
        //queryBuilder.withHighlightBuilder()  添加高亮配置
        queryBuilder.withHighlightBuilder(new HighlightBuilder()
                .field("title")
                .preTags("<em>")
                .postTags("</em>")
            );
        // queryBuilder.addAggregation() 聚合度量配置
        //根据品牌聚合品牌内使用分类聚合 最后对分类聚合结果度量（平均价格）
        //terms方法表示使用词条聚合，参数为聚合后通的名称
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brandAggs").field("attr.brand")
                    .subAggregation(AggregationBuilders.terms("categoryAggs").field("attr.category")
                        .subAggregation(AggregationBuilders.avg("avgPrice").field("price")))
        );

        //参数1：构建查询的dsl语句的对象
        //参数2：查询后的结果封装的类型
        //参数3：要操作的es中的索引库列表
        SearchHits<Goods> searchHits = restTemplate.search(queryBuilder.build(), Goods.class, IndexCoordinates.of("goods"));
        //searchHits代表查询到的整个结果集
        //searchHits.getSearchHits() 代表获取结果集中hits中的文档结果
        //searchHits.getAggregations() 代表获取聚合度量结果
        searchHits.getSearchHits().forEach(hit-> {
            //hit代表一个匹配的文档的完整数据（文档+高亮字段+es文档默认）
            Goods goods = hit.getContent();
//            System.out.println(hit.getHighlightField("title").get(0));
//            goods.setTitle(hit.getHighlightField("title").get(0));
            System.out.println(goods);
        });
        //获取聚合结果：使用第一层聚合的桶名为键 聚合结果作为值存到map中
        Map<String, Aggregation> map = searchHits.getAggregations().asMap();
        ParsedStringTerms brandAggs = (ParsedStringTerms)map.get("brandAggs");  //获取第一层聚合的结果对象
//        System.out.println(brandAggs+","+brandAggs.getClass().getName());

        //获取聚合的品牌桶
        List<? extends Terms.Bucket> brandAggsBuckets = brandAggs.getBuckets();
        brandAggsBuckets.forEach(brandAggsBucket -> {
            //获取皮牌通中嵌套的分类桶
            ParsedStringTerms categoryAggs = (ParsedStringTerms)brandAggsBucket.getAggregations().asMap().get("categoryAggs");
            //得到分类桶集合
            List<? extends Terms.Bucket> categoryAggsBuckets = categoryAggs.getBuckets();
            categoryAggsBuckets.forEach(categoryAggsBucket -> {
                System.out.println("key："+categoryAggsBucket.getKey());
                System.out.println("docCount："+categoryAggsBucket.getDocCount());
                //获取分类桶中的度量结果
                ParsedAvg avgPriceAggs = (ParsedAvg)categoryAggsBucket.getAggregations().asMap().get("avgPrice");
//                System.out.println(avgPriceAggs.getClass().getName());
                System.out.println(avgPriceAggs.getType());
                System.out.println(avgPriceAggs.getValue());
                System.out.println("========================================");

            });
        });

    }
}
