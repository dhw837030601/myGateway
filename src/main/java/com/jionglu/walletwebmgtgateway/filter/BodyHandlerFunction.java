package com.jionglu.walletwebmgtgateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

/**
 * @author 段华微
 * @version V1.0
 * @description 响应body拦截处理接口
 * @ClassName: BodyHandlerFunction
 * @Date 2018/12/25 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
public interface BodyHandlerFunction extends
        BiFunction<ServerHttpResponse, Publisher<? extends DataBuffer>, Mono<Void>> {
}
