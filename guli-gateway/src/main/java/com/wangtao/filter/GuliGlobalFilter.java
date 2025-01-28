package com.wangtao.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//获取请求路径和客户端ip输出
//全局过滤器
@Component
public class GuliGlobalFilter implements GlobalFilter, Ordered {

    //过滤
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        AntPathMatcher matcher = new AntPathMatcher();
        boolean flag = matcher.match("/order/**", path);
        System.out.println("客户端请求的路径："+path);
        System.out.println("请求头host:"+request.getHeaders().getFirst("host"));
        return chain.filter(exchange);
//
//        if(flag){
//            return chain.filter(exchange);
//        }else{
//            ServerHttpResponse response = exchange.getResponse();
//            response.setStatusCode(HttpStatus.BAD_REQUEST);
//            return response.setComplete();
//        }
    }

    //过滤器优先级：所有的全局过滤器 优先级高的先执行
    //值越小优先级越高
    @Override
    public int getOrder() {
        return 1;
    }
}
