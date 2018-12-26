package com.jionglu.walletwebmgtgateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author 段华微
 * @version V1.0
 * @description 1.此类实现ServerHttpResponseDecorator
 *              2.此类包含BodyHandlerFunction用于body的处理转化
 * @ClassName: BodyHandlerServerHttpResponseDecorator
 * @Date 2018/12/25 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
public class BodyHandlerServerHttpResponseDecorator
        extends ServerHttpResponseDecorator {

    /**
     * body 处理拦截器
     */
    private BodyHandlerFunction bodyHandler = initDefaultBodyHandler();

    /**
     * 构造函数
     *
     * @param bodyHandler
     * @param delegate
     */
    public BodyHandlerServerHttpResponseDecorator(
            BodyHandlerFunction bodyHandler, ServerHttpResponse delegate) {
        super(delegate);
        if (bodyHandler != null) {
            this.bodyHandler = bodyHandler;
        }
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        //body 拦截处理器处理响应
        return bodyHandler.apply(getDelegate(), body);
    }

    @Override
    public Mono<Void> writeAndFlushWith(
            Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMapSequential(p -> p));
    }

    /**
     * 默认body拦截处理器
     *
     * @return
     */
    private BodyHandlerFunction initDefaultBodyHandler() {
        return (resp, body) -> resp.writeWith(body);
    }
}
