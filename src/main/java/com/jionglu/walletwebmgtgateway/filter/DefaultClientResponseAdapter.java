/*
package com.jionglu.walletwebmgtgateway.filter;

*/
/**
 * @author 段华微
 * @version V1.0
 * @description 描述
 * @ClassName: PayOrderStatusEnum
 * @Date 2018/12/25 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 *//*


import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

*/
/**
 * ClientResponse适配器，用于将body转换为DefaultClientResponse对象进而对body流反序列化
 *
 *//*

public class DefaultClientResponseAdapter extends org.springframework.web.reactive.function.client.DefaultClientResponse {

    */
/**
     * @param body
     * @param httpHeaders
     *//*

    public DefaultClientResponseAdapter(Publisher<? extends DataBuffer> body,
                                        HttpHeaders httpHeaders) {
        this(new ResponseAdapter(body, httpHeaders),
                ExchangeStrategies.withDefaults());
    }

    */
/**
     * @param response
     * @param strategies
     *//*

    public DefaultClientResponseAdapter(ClientHttpResponse response,
                                        ExchangeStrategies strategies) {
        super(response, strategies);
    }

    */
/**
     * ClientHttpResponse 适配器
     *//*

    static class ResponseAdapter implements ClientHttpResponse {
        */
/**
         * 响应数据
         *//*

        private final Flux<DataBuffer> flux;
        */
/**
         *
         *//*

        private final HttpHeaders headers;

        public ResponseAdapter(Publisher<? extends DataBuffer> body,
                               HttpHeaders headers) {
            this.headers = headers;
            if (body instanceof Flux) {
                flux = (Flux) body;
            } else {
                flux = ((Mono) body).flux();
            }
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return flux;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public HttpStatus getStatusCode() {
            return null;
        }

        @Override
        public int getRawStatusCode() {
            return 0;
        }

        @Override
        public MultiValueMap<String, ResponseCookie> getCookies() {
            return null;
        }
    }
}

*/
