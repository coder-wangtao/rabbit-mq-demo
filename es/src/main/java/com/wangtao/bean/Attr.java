package com.wangtao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Attr {
    @Field(name = "brand",type = FieldType.Keyword)
    private String brand;
    @Field(name = "category",type = FieldType.Keyword)
    private String category;
}
