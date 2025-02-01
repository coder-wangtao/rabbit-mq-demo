package com.wangtao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "goods", shards = 3, replicas = 2)  //指定类映射的索引库名+分片备份数量
public class Goods {
    @Id  //表示当前属性为主键
    private Long id;
    // name:映射的es中字段名称，type:类型Text字符串 会分词 analyzer：配置分词器
    @Field(name = "title",type = FieldType.Text,analyzer = "ik_max_word")
    private String title;
    //index表示不创建倒排索引
    @Field(name = "images",type = FieldType.Keyword,index = false)
    private String images;
    @Field(name = "price",type = FieldType.Double)
    private Double price;
    @Field(name = "stock",type = FieldType.Integer)
    private Integer stock;
    //FieldType.Nested 嵌套类型 自定义一般使用Nested 避免数据扁平化
    @Field(name = "attr",type = FieldType.Object)
    private Attr attr;
//
//    @Field(ignoreAbove = 0)
//    private String highlightTitle;
}
