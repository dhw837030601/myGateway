package com.jionglu.walletwebmgtgateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.spring.annotation.MapperScan;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.jionglu.walletwebmgtgateway.mapper")
public class WalletWebMgtGatewayApplication {
    private static Logger logger = LoggerFactory.getLogger(WalletWebMgtGatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WalletWebMgtGatewayApplication.class, args);
//        测试是否能ping通阿里云数据库
//        String line = null;
//        try {
//            Process pro = Runtime.getRuntime().exec("ping " + "rdsaibairjvqvezbo.mysql.rds.aliyuncs.com");
//            BufferedReader buf = new BufferedReader(new InputStreamReader(
//                    pro.getInputStream()));
//            while ((line = buf.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getMessage());
//        }
    }
    @RequestMapping("/hystrixfallback")
    public String hystrixfallback() {
        return "This is a fallback";
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        //@formatter:off
        return builder.routes()
//                .route("risk", r -> r.path("/risk/**").filters(f-> f.prefixPath("").filter(new AuthCheckGatewayFilter()))
//                        .uri("http://127.0.0.1:8093"))
                .route("host_route", r -> r.host("*.myhost.org")
                        .uri("http://httpbin.org"))
                .route("rewrite_route", r -> r.host("*.rewrite.org")
                        .filters(f -> f.rewritePath("/foo/(?<segment>.*)",
                                "/${segment}"))
                        .uri("http://httpbin.org"))
                .route("hystrix_route", r -> r.host("*.hystrix.org")
                        .filters(f -> f.hystrix(c -> c.setName("slowcmd")))
                        .uri("http://httpbin.org"))
                .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
                        .filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
                        .uri("http://httpbin.org"))
                .route("limit_route", r -> r
                        .host("*.limited.org").and().path("/anything/**")
                        .filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
                        .uri("http://httpbin.org"))
                .route("websocket_route", r -> r.path("/echo")
                        .uri("ws://localhost:9000"))
//                .route("test",r -> r.path("/test")
//                        .uri("http://httpbin.org:80"))
//                .route("test")
//                .uri("http://httpbin.org:80")
//                .predicate(host("**.abc.org").and(path("/image/png")))
//                .addResponseHeader("X-TestHeader", "foobar")
//                .and()
//                .route("test2")
//                .uri("http://httpbin.org:80")
//                .predicate(path("/image/webp"))
//                .add(addResponseHeader("X-AnotherHeader", "baz"))
//                .and()
                .build();
        //@formatter:on
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 2);
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http.httpBasic().and()
                .authorizeExchange()
                .pathMatchers("/anything/**").authenticated()
                .anyExchange().permitAll()
                .and()
                .csrf()
                .disable()  // 禁用 Spring Security 自带的跨域处理
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService reactiveUserDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
        return new MapReactiveUserDetailsService(user);
    }
}
