#查看es中默认的索引（类似mysql的数据库）
GET /_cat/indices?v

#es默认对中文的分词支持不友好，他认为一个字代表一个词
# 查看es的分词方式
GET _analyze
{
    "text": "我是中国人"
}

#给es配置中文分词器：ik分词器
#指定ik分词器：ik_smart粗粒度分词
#ik_max_word细粒度分词   
GET _analyze
{
    "text": "王涛，你这个老六，咖喱人，鸡你太美",
    "analyzer": "ik_max_word"
}


#es也是存储数据的工具
#索引 index  =>  mysql 库和表（index相当于有一张表的库）
#映射 mapping ==> mysql 表结构
#文档 document ==> msyql的一行记录

#先创建索引库index,给索引库创建mapping映射
#按照mapping映射的要求，创建文档存入es中

GET /_cat/indices?v
DELETE /atguigu
PUT /atguigu


# number_of_shards 分片数量
# number_of_replicas 副本数量
PUT /atguigu
{
    "settings": {
        "number_of_shards": 3,
        "number_of_replicas": 2,
        "blocks.read_only_allow_delete":"false"
    }
}

#给索引创建映射：类似表结构（字段：类型、索引）
#字符串字段类型：text（内容会被分词）。keyword(不会被分词)
#analyzer:给分词的字段指定分词器，如果不配置则使用默认的
#index:默认为true,是否建立倒排索引
POST /atguigu/_mapping
{
    "properties":{
        "title":{
            "type":"text",
            "analyzer":"ik_max_word"
        },
        "images":{
            "type":"keyword",
            "index":false
        },
        "price":{
            "type":"long"
        }
    }
}

GET /atguigu/_settings
PUT /atguigu/_settings
{
    "index.blocks.write": false
}



#查看索引库的映射
GET /atguigu/_mapping
#新增文档
POST /atguigu/_doc
{
    "price":1999,
    "title":"小米手机11promax",
    "images":"http://xiaomi.jpg"
}
POST /atguigu/_doc/1
{
    "price":2999,
    "title":"红米手机11promax",
    "images":"http://hongmi.jpg"
}
DELETE /atguigu/_doc/3z7rupQBt4OiP6gLwfYK

# 查看索引库中的所有文档
GET /atguigu/_search
# 修改文档:只修改指定的属性值
POST /atguigu/_update/1
{
    "doc": {
        "price":2991
    }
}
# 修改文档：替换之前的文档内容
PUT /atguigu/_doc/1
{
    "price":2999
}

#es可以根据新增文档的内容 智能判断扩展映射
# 字符串类型会生成text类型和属性的keyword子类型
POST /atguigu/_doc/2
{
    "title":"小米手机",
    "images":"http://image.jd.com/12479122.jpg",
    "price":2899,
    "stock": 200,
    "saleable":true,
    "attr": {
        "category": "手机",
        "brand": "小米"
    }
}

#批量导入数据
POST /atguigu/_bulk
{"index":{"_id":1}}
{ "title":"小米手机", "images":"http://image.jd.com/12479122.jpg", "price":1999, "stock": 200, "attr": { "category": "手机", "brand": "小米" } }
{"index":{"_id":2}}
{"title":"超米手机", "images":"http://image.jd.com/12479122.jpg", "price":2999, "stock": 300, "attr": { "category": "手机", "brand": "小米" } }
{"index":{"_id":3}}
{ "title":"小米电视", "images":"http://image.jd.com/12479122.jpg", "price":3999, "stock": 400, "attr": { "category": "电视", "brand": "小米" } }
{"index":{"_id":4}}
{ "title":"小米笔记本", "images":"http://image.jd.com/12479122.jpg", "price":4999, "stock": 200, "attr": { "category": "笔记本", "brand": "小米" } }
{"index":{"_id":5}}
{ "title":"华为手机", "images":"http://image.jd.com/12479122.jpg", "price":3999, "stock": 400, "attr": { "category": "手机", "brand": "华为" } }
{"index":{"_id":6}}
{ "title":"华为笔记本", "images":"http://image.jd.com/12479122.jpg", "price":5999, "stock": 200, "attr": { "category": "笔记本", "brand": "华为" } }
{"index":{"_id":7}}
{ "title":"荣耀手机", "images":"http://image.jd.com/12479122.jpg", "price":2999, "stock": 300, "attr": { "category": "手机", "brand": "华为" } }
{"index":{"_id":8}}
{ "title":"oppo手机", "images":"http://image.jd.com/12479122.jpg", "price":2799, "stock": 400, "attr": { "category": "手机", "brand": "oppo" } }
{"index":{"_id":9}}
{ "title":"vivo手机", "images":"http://image.jd.com/12479122.jpg", "price":2699, "stock": 300, "attr": { "category": "手机", "brand": "vivo" } }
{"index":{"_id":10}}
{ "title":"华为nova手机", "images":"http://image.jd.com/12479122.jpg", "price":2999, "stock": 300, "attr": { "category": "手机", "brand": "华为" } }

#1.查询所有
GET /atguigu/_search

#2.分词查询
# 分词后词之前时or的关系
GET _analyze
{
    "text": "小米电视",
    "analyzer": "ik_max_word"
}

GET /atguigu/_search
{
    "query": {
        "match": {
            "title": "小米手机"
        }
    }
}

GET /atguigu/_search
{
    "query": {
        "match": {
            "title": {
                "query": "小米手机",
                "operator": "and"
            }
        }
    }
}

#2.词条查询:使用完整的词条整体查询不分词（keyword和基本类型的）
#kewword类型的词条必须创建索引才可以查询
GET /atguigu/_search
{
    "query": {
        "term": {
            "images": "http://image.jd.com/12479122.jpg"
        }
    }
}
#attr.brand映射中的类型时text,会分词，使用的时默认分词器
#attr.brand.keyword得到的是智能判断生成的keyword类型的属性，不分词
GET /atguigu/_search
{
    "query": {
        "term": {
            "attr.brand.keyword": "小米"
            }
        }
}

# 多词条查询
GET /atguigu/_search
{
    "query": {
        "terms": {
            "attr.brand.keyword": [
                "小米","华为"      
            ]
        }
    }
}

#4.模糊查询
#默认es模糊查询字符偏移量为0，最大可以设置为2
#偏移量：搜索数据到文档中分词后的一个词需要编辑的次数
GET /atguigu/_search
{
    "query": {
        "fuzzy": {
            "title": {
                "value": "大米",
                "fuzziness": 1
            }
        }
    }
}

#5 范围查询
GET /atguigu/_search
{
    "query": {
        "range": {
            "price": {
                "gte": 1999,
                "lte": 1999
            }
        }
    }
}

#6 布尔组合查询
#可以将上面多种查询组合起来多条件查询
# 查询价格低于2999标题中包含小米手机并且品牌是小米的商品
GET _search
{
    "query": {
        "bool": {
        "must": [
            {
            "range":{
                "price":{
                    "lte":4999
                }
            }
        ]
    }
},
{
    "match":{
        "title":"华为手机"
    }
},
{
"term":{
"attr.brand.keyword":{
"value":"小米"
}
}
}
]
}
}
}

#7 布尔组合-过滤查询
# 过滤查询可以将布尔组合中查询提取到filter中，效果和之前一样但是不影响文档的评分
# 查询价格低于2999标题中包含小米手机并且品牌是小米的商品

GET /atguigu/_search
{
"query": {
"bool": {
"must": [
{
"match": {
"title": "华为手机"
}
}
],
"filter": [
{
"range": {
"price": {
"lte": 4999
}
}
},
{
"term": {
"attr.brand.keyword": "小米"
}
}
]
}
}
}

#分页查询
GET /atguigu/_search
{
"query": {
"match_all": {}
},
"from": 0,
"size": 3
}

#排序查询
#先使用第一个字段排序，第一个字段值相同时，在使用第二个字段排序
GET /atguigu/_search
{
"sort": [
{
"price": {
"order": "asc"
}
},
{
"stock": {
"order": "desc"
}
}

]
}

#高亮查询
#将匹配查询到的字段词，在词的前后拼接标签字符串，前端对标签设置样式即可
GET /atguigu/_search
{
"query": {
"match": {
"title": "华为手机"
}
},
"highlight": {
"fields": {"title":{}},
"pre_tags": "<font style='color:red;'>",
"post_tags": "</font>"
}
}

#结果过滤：只查询指定的属性
GET /atguigu/_search
{
"_source":["title","price"]
}


#聚合：聚合为桶
#分组：使用品牌聚合

GET /atguigu/_search
{
"size": 0,  #一条记录都不查
"aggs": {
"brandAggs": {
"terms": {
"field": "attr.brand.keyword"
}
},
"categoryAggs": {
"terms": {
"field": "attr.category.keyword"}
}
}
}

# 桶内度量
# 计算品牌和分类桶中每个桶内产品的平均价格
GET /atguigu/_search
{
"size": 0,
"aggs": {
"brandAggs": {
"terms": {
"field": "attr.brand.keyword"
},
"aggs": {
"avg_price": {
"avg": {
"field": "price"
}
}
}
},
"categoryAggs": {
"terms": {
"field": "attr.category.keyword"
},
"aggs": {
"max_price": {
"max": {
"field": "price"
}
},
"min_price": {
"min": {
"field": "price"
}
}
}
}
}
}

# 桶内嵌套桶+桶内度量
GET /atguigu/_search
{
"size": 0,
"aggs": {
"brandAggs": {
"terms": {
"field": "attr.brand.keyword"
},
"aggs": {
"categoryAggs": {
"terms": {
"field": "attr.category.keyword"
},
"aggs": {
"avgPrice": {
"avg": {
"field": "price"
}
}
}
}
}
}
}
}


#查询有货的小米手机前5条记录，标题关键字高亮显示，按照价格排序，只要标题价格图片属性，并查询品牌分类桶
GET /atguigu/_search
{
"query": {
"bool": {
"must": [
{
"match": {
"title": "小米手机"
}
}
],
"filter": [
{
"range": {
"stock": {
"gt": 0
}
}
}
]
}
},
"sort":[
{
"price":{
"order":"asc"
}
}
],
"from":0,
"size":5,
"_source":["title","images","price"],
"highlight": {
"fields": {"title": {}},
"pre_tags": "<em>",
"post_tags": "</em>"
},
"aggs": {
"brandAggs": {
"terms": {
"field": "attr.brand.keyword"
}
},
"cateAggs": {
"terms": {
"field": "attr.category.keyword"
}
}
}
}

