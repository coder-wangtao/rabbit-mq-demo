package com.wangtao.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.wangtao.entity.StockEntity;
import com.wangtao.feign.StockClient;
import com.wangtao.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

@RestController
@RefreshScope  //刷新当前类中的配置
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    private StockClient stockClient;

    //假设配置中心提供了一个配置参数：app.name = xxx
    @Value("${app.name}")
    private String appName;

    @Value("${mysql.url}")
    private String mysqlUrl;

    @Value("${mysql.username}")
    private String mysqlUsername;

    @Value("${redis.host}")
    private String host;


    @GetMapping("/test")
    @SentinelResource(value = "testResource",blockHandler = "deadTest",blockHandlerClass = OrderController.class)
    public String test(@RequestParam(value = "pid",required = false) String pid,
                       @RequestParam(value = "count",required = false) String count) {
        System.out.println("购买商品的id为："+pid+",数量："+count);
        return "test";
    }


    //配置中心可以将项目的参数提取到配置中心
    //项目使用时就像自己本地文件中配置一样
    @GetMapping("testConfig")
    public String testConfig(){
        System.out.println(appName);
        System.out.println(mysqlUrl);
        System.out.println(mysqlUsername);
        System.out.println("加载reds配置"+host);
        return appName;
    }
    @GetMapping("{userId}/{productId}/{count}")
    public Boolean createOrder(@PathVariable("userId")String userId,
                               @PathVariable("productId")String productId,
                               @PathVariable("count")Integer count){
//        String str = stockClient.test1("zhangsan","123456");
//        System.out.println(str);
//        StockEntity stockEntity = new StockEntity();
//        stockEntity.setId(111);
//        stockEntity.setTitle("111");
//        stockClient.test3(stockEntity);
//        return true;

//        return true;
        return orderService.saveOrder(userId,productId,count);
    }

    //热点key限流后的兜底方案
    //兜底方法必须是静态的 参数列表一定要按照顺序接受热点key接口方法 一定要接受限流异常
    public static String deadTest(String pid, String count, BlockException blockException){
        System.out.println("pid:"+pid+",count:"+count);
        System.out.println("blockException:"+blockException);
        return "deadTest";
    }


}
