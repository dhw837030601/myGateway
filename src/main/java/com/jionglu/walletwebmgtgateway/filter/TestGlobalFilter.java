package com.jionglu.walletwebmgtgateway.filter;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author 段华微
 * @version V1.0
 * @description 描述
 * @ClassName: TestGlobalFilter
 * @Date 2018/12/25 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
//@Component
public class TestGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        System.out.println("Test1GlobalFilter进入此方法");
        StringBuilder stringBuilder = new StringBuilder();
        //构建响应拦截处理器
        BodyHandlerFunction bodyHandler = (resp, body) -> Flux.from(body)
                .map(dataBuffer -> {
                    //响应信息转换为字符串
                    String reqBody = null;
                    try {
                        //dataBuffer 转换为String
                        reqBody = IOUtils
                                .toString(dataBuffer.asInputStream(), "UTF-8")
                                .replaceAll(">\\s{1,}<", "><");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return reqBody;
                }).flatMap(orgBody -> {
                    //根据原有的响应信息构建新响应信息并写入到resp中
                    //此处可以根据业务进行组装新的响应信息，
                    // 例如：登录后创建会话
                    //- 拿到登录请求的响应信息，判断登录是否成功
                    //- 登录成功调用创建会话接口，根据创建会话接口返回组装登录的真实响应
                    System.out.println("orgBody=[" + orgBody+"]end");
                    stringBuilder.append(orgBody);
                    try{
                        JSONObject jsonObject = JSONObject.parseObject(stringBuilder.toString());
                        DataBuffer bodyDataBuffer = resp.bufferFactory().wrap(stringBuilder.toString().getBytes());
                        return resp.writeWith(Mono.just(bodyDataBuffer));
                    }catch (Exception e){
                        System.out.println("response-不完整");
                        e.printStackTrace();
                    }
                    HttpHeaders headers = resp.getHeaders();
                    headers.setContentLength(orgBody.length());
                    return resp.writeWith(Flux.just(orgBody)
                            .map(bx -> resp.bufferFactory()
                                    .wrap(bx.getBytes())));
                }).then();

        //构建响应包装类
        BodyHandlerServerHttpResponseDecorator responseDecorator = new BodyHandlerServerHttpResponseDecorator(
                bodyHandler, exchange.getResponse());
        return chain
                .filter(exchange.mutate().response(responseDecorator).build());
    }

    @Override
    public int getOrder() {
        //WRITE_RESPONSE_FILTER 之前执行
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }

}
