package com.wangtao.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

//局部过滤器
@Component
public class GuliAddTokenGatewayFilterFactory extends AbstractGatewayFilterFactory {

    @Override
    public GatewayFilter apply(Object config) {
       return new GatewayFilter() {
           @Override
           public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
               ServerHttpRequest request = exchange.getRequest();
               //修改request和response时必须调用mutate方法修改
               request.mutate().header("cookie","token="+ UUID.randomUUID().toString().replace("-",""))
                       .build();
               //将修改后的request设置到交换机中构建新的exchange对象
               exchange.mutate().request(request).build();
               return chain.filter(exchange);
           }
       };
    }

    @Override
    public String name() {
        return "addToken";
    }
}
