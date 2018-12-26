package com.jionglu.walletwebmgtgateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.jionglu.common.consts.IsSuccessEnum;
import com.jionglu.common.entity.operationLog.OperationLog;
import com.jionglu.common.entity.staff.Staff;
import com.jionglu.common.entity.sys.PrivAction;
import com.jionglu.common.exception.ErrorEnum;
import com.jionglu.common.exception.WalletException;
import com.jionglu.common.utils.AssembleErrJsonUtil;
import com.jionglu.walletwebmgtgateway.operationLog.service.OperationLogService;
import com.jionglu.walletwebmgtgateway.privAction.service.PrivActionService;
import com.jionglu.walletwebmgtgateway.staff.service.StaffService;
import com.jionglu.walletwebmgtgateway.util.OperationLogUtils;
import io.netty.buffer.ByteBufAllocator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author 段华微
 * @version V1.0
 * @description 描述
 * @ClassName: AuthCheckGatewayFilter
 * @Date 2018/6/11 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
@Component
public class AuthCheckGatewayFilter implements GlobalFilter/*GatewayFilter*/,Ordered {

    private static Logger logger = LoggerFactory.getLogger(AuthCheckGatewayFilter.class);

    private StaffService staffService;

    private PrivActionService privActionService;

    private OperationLogService operationLogService;

    //设置不拦截的URL
    private List<String> excludedUrls = Arrays.asList(
            "/system/login/login",
            "/media/",
            "authority/refreshaction",
            "/system/role/getAllRoleListByNoValidate",
            "web/content/saveToDrafts",
            "web/content/publish"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        staffService = exchange.getApplicationContext().getBean(StaffService.class);
        privActionService = exchange.getApplicationContext().getBean(PrivActionService.class);
        operationLogService = exchange.getApplicationContext().getBean(OperationLogService.class);

        ServerHttpRequest request = exchange.getRequest();
        //获取response--------------
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("X-Content-Type-Options","");
        response.setStatusCode(HttpStatus.OK);
        //获取path-----------------------
        RequestPath allPath = request.getPath();
        String path = allPath.pathWithinApplication().value();
        System.out.println(path);

        PrivAction action = null;
        try {
            action = privActionService.getPrivActionByAction(path);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("gateway查找action不存在！");
        }
        String rtn = "";
        if (action == null){
            rtn = AssembleErrJsonUtil.AssembleErr2Json(ErrorEnum.ERR_PRIVACTION_IS_NOTFOUND,"");
            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(rtn.getBytes());
            System.out.println(rtn);
            return response.writeWith(Mono.just(bodyDataBuffer));
        }
        //操作日志
        OperationLog operationLog = OperationLogUtils.genOperationLog(request);
        operationLog.setOperationName(action.getActionName());
        try {
            operationLogService.addOperationLog(operationLog);
        } catch (WalletException e) {
            e.printStackTrace();
            logger.error("gateway插入操作日志失败！");
        }
        //不拦截资源文件
        for (String url : excludedUrls) {
            if(path.contains(url)){
                //判断接口是否访问成功
                ServerHttpResponse originalResponse = exchange.getResponse();
                if (HttpStatus.OK.equals(originalResponse.getStatusCode())) {
                    DataBufferFactory bufferFactory = originalResponse.bufferFactory();
                    ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                        @Override
                        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                            if (body instanceof Flux) {
                                Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                                return super.writeWith(fluxBody.map(dataBuffer -> {
                                    // probably should reuse buffers
                                    byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    //释放掉内存
                                    DataBufferUtils.release(dataBuffer);
                                    String s = new String(content, Charset.forName("UTF-8"));
                                    System.out.println("接口返回结果："+s);
                                    JSONObject obj = new JSONObject();
                                    try{
                                        obj = JSONObject.parseObject(s);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        System.out.println("返回结果转换成jsonObject失败");
                                    }
                                    OperationLog temp = new OperationLog();
                                    temp.setId(operationLog.getId());
                                    temp.setUpdateTime(new Date());
                                    if ("000000".equals(obj.get("ecode"))) {
                                        temp.setIsSuccess(IsSuccessEnum.SUCCESS.getValue());
                                    } else {
                                        temp.setIsSuccess(IsSuccessEnum.FAILED.getValue());
                                    }
                                    try {
                                        operationLogService.updateOperationLog(temp);
                                    } catch (WalletException e) {
                                        e.printStackTrace();
                                        System.out.println("gateway更新操作日志失败！");
                                    }
                                    byte[] uppedContent = new String(content, Charset.forName("UTF-8")).getBytes();
                                    return bufferFactory.wrap(uppedContent);
                                }));
                            }
                            // if body is not a flux. never got there.
                            return super.writeWith(body);
                        }
                    };
                    // replace response with decorator
                    return chain.filter(exchange.mutate().response(decoratedResponse).build());
                }else{
                    return chain.filter(exchange);
                }
            }
        }
        //获取参数
        String staffIdStr = request.getQueryParams().getFirst("staffId");
        String accessToken = request.getQueryParams().getFirst("accessToken");
        String callbackFunName = request.getQueryParams().getFirst("callbackparam");
        String roleId = request.getQueryParams().getFirst("roleId");
        System.out.println("权限验证：参数--->staffId="+staffIdStr+",accessToken="+accessToken+",roleId="+roleId);
        //判断参数---------------------
        //TODO 传roleId后修改
        if (StringUtils.isBlank(staffIdStr) || StringUtils.isBlank(accessToken) /*|| StringUtils.isBlank(roleId)*/){
            String msg = AssembleErrJsonUtil.AssembleErr2Json(ErrorEnum.ERR_PARAM_PARAMETER_IS_NULL,callbackFunName);
            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(msg.getBytes());
            System.out.println(msg);
            return response.writeWith(Mono.just(bodyDataBuffer));
        }
        Staff staff = staffService.getStaffById(Integer.parseInt(staffIdStr));

        if(staff == null){
            rtn = AssembleErrJsonUtil.AssembleErr2Json(ErrorEnum.ERR_STAFF_IS_NOTFOUND,callbackFunName);
            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(rtn.getBytes());
            System.out.println(rtn);
            return response.writeWith(Mono.just(bodyDataBuffer));
        }
        OperationLog log = new OperationLog();
        log.setId(operationLog.getId());
        log.setStaffId(staff.getId());
        log.setUserName(staff.getNickName());
        log.setUpdateTime(new Date());
        try {
            operationLogService.updateOperationLog(log);
        } catch (WalletException e) {
            e.printStackTrace();
            System.out.println("gateway更新操作日志失败！");
        }
        boolean isExist = false;
        //TODO 传roleId后修改
        List<PrivAction> list = new ArrayList<>();
        if(StringUtils.isNotBlank(roleId)){
            list = staffService.getActionListByStaffIdAndCategory(Integer.parseInt(staffIdStr),2,Integer.parseInt(roleId));
        }else {
            list = staffService.getActionListByStaffIdAndCategory(Integer.parseInt(staffIdStr),2,null);
        }
        if(CollectionUtils.isNotEmpty(list)){
            for(PrivAction p:list){
                if(path.equals(p.getAction())){
                    isExist = true;
                    break;
                }
            }
        }
        //如果该用户没有访问的action或者访问的action没有权限，则报错
        if(CollectionUtils.isEmpty(list) || !isExist){
            rtn = AssembleErrJsonUtil.AssembleErr2Json(ErrorEnum.ERR_THIS_ROLE_HAS_NOT_THIS_ACTION,callbackFunName);
            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(rtn.getBytes());
            System.out.println(rtn);
            return response.writeWith(Mono.just(bodyDataBuffer));
        }
        //判断接口是否访问成功
        ServerHttpResponse originalResponse = exchange.getResponse();
        if (HttpStatus.OK.equals(originalResponse.getStatusCode())) {
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.map(dataBuffer -> {
                            // probably should reuse buffers
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            //释放掉内存
                            DataBufferUtils.release(dataBuffer);
                            String s = new String(content, Charset.forName("UTF-8"));
                            System.out.println("接口返回结果："+s);
                            JSONObject obj = new JSONObject();
                            try{
                                obj = JSONObject.parseObject(s);
                            }catch (Exception e){
                                e.printStackTrace();
                                System.out.println("返回结果转换成jsonObject失败");
                                obj.put("ecode","000000");
                            }
                            OperationLog temp = new OperationLog();
                            temp.setId(operationLog.getId());
                            temp.setUpdateTime(new Date());
                            if ("000000".equals(obj.get("ecode"))) {
                                temp.setIsSuccess(IsSuccessEnum.SUCCESS.getValue());
                            } else {
                                temp.setIsSuccess(IsSuccessEnum.FAILED.getValue());
                            }
                            try {
                                operationLogService.updateOperationLog(temp);
                            } catch (WalletException e) {
                                e.printStackTrace();
                                System.out.println("gateway更新操作日志失败！");
                            }
                            byte[] uppedContent = new String(content, Charset.forName("UTF-8")).getBytes();
                            return bufferFactory.wrap(uppedContent);
                        }));
                    }
                    // if body is not a flux. never got there.
                    return super.writeWith(body);
                }
            };
            // replace response with decorator
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }else{
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -200;//数字越小优先级越高
    }

    protected DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
}
