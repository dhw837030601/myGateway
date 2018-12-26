package com.jionglu.walletwebmgtgateway.util;

import com.jionglu.common.consts.BrowerTypeEnum;
import com.jionglu.common.consts.OsTypeEnum;
import com.jionglu.common.entity.operationLog.OperationLog;
import com.jionglu.common.utils.IpUtils;
import com.jionglu.common.utils.UserAgentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.util.Date;

/**
 * @author 段华微
 * @version V1.0
 * @description 描述
 * @ClassName: 操作日志工具类
 * @Date 2018/10/29 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
public class OperationLogUtils {

    private static Logger logger = LoggerFactory.getLogger(OperationLogUtils.class);
    /**
     *
     * @description 组装操作日志
     * @param
     * @return    返回类型
     * @author 段华微
     * @date 2018-10-29 14:41:41
     */
    public static OperationLog genOperationLog(ServerHttpRequest request){
        OperationLog log = new OperationLog();
        //获取请求方法
        HttpMethod method = request.getMethod();
        String methodValue = request.getMethodValue();
        //获取path-----------------------
        RequestPath allPath = request.getPath();
        String path = allPath.pathWithinApplication().value();
        logger.debug("path:"+path);
        log.setMethodType(method.toString());
        log.setUrl(path);
        log.setParams(request.getQueryParams().toString());
        InetAddress address = request.getRemoteAddress().getAddress();
        String ip = address.toString().substring(1);
        log.setIp(ip);
        log.setIpAddress(IpUtils.getIpInfoByIp(ip));
        HttpHeaders headers = request.getHeaders();
        String userAgent = headers.get("User-Agent").get(0);
        //浏览器类型
        BrowerTypeEnum browserType = UserAgentUtils.GetBrowserType(userAgent);
        log.setBrowser(browserType.getName());
        //操作系统类型
        OsTypeEnum osType = UserAgentUtils.GetOSType(userAgent);
        log.setOperateSystem(osType.getName());
        Date date = new Date();
        log.setOperateTime(date);
        log.setCreateTime(date);
        log.setUpdateTime(date);
        return log;
    }
}
