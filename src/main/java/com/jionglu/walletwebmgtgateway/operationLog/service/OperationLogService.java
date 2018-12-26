package com.jionglu.walletwebmgtgateway.operationLog.service;

import com.jionglu.common.entity.operationLog.OperationLog;
import com.jionglu.common.exception.WalletException;

/**
 * @author 段华微
 * @version V1.0
 * @description 操作日志接口
 * @ClassName: OperationLogService
 * @Date 2018/10/29 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
public interface OperationLogService {
    /**
     *
     * @description 添加操作日志
     * @param
     * @return    返回类型
     * @author 段华微
     * @date 2018-10-29 14:19:56
     */
    void addOperationLog(OperationLog log) throws WalletException;
    /**
     *
     * @description 修改操作日志
     * @param
     * @return    返回类型
     * @author 段华微
     * @date 2018-10-29 15:25:47
     */
    void updateOperationLog(OperationLog log) throws WalletException;
}
