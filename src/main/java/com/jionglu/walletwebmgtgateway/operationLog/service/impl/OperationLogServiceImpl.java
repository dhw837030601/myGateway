package com.jionglu.walletwebmgtgateway.operationLog.service.impl;

import com.jionglu.common.entity.operationLog.OperationLog;
import com.jionglu.common.exception.ErrorEnum;
import com.jionglu.common.exception.WalletException;
import com.jionglu.walletwebmgtgateway.mapper.operationLog.OperationLogMapper;
import com.jionglu.walletwebmgtgateway.operationLog.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 段华微
 * @version V1.0
 * @description 操作日志service
 * @ClassName: OperationLogServiceImpl
 * @Date 2018/10/29 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     *
     * @description 添加操作日志
     * @param
     * @return    返回类型
     * @author 段华微
     * @date 2018-10-29 14:22:52
     */
    @Override
    public void addOperationLog(OperationLog log) throws WalletException {
        int rtn = operationLogMapper.insertSelective(log);
        if (rtn <= 0){
           throw new WalletException(ErrorEnum.ERR_DB_ADD_DATA_ERR);
        }
    }
    /**
     *
     * @description 更新操作日志
     * @param
     * @return    返回类型
     * @author 段华微
     * @date 2018-10-29 15:26:11
     */
    @Override
    public void updateOperationLog(OperationLog log) throws WalletException {
        int rtn = operationLogMapper.updateByPrimaryKeySelective(log);
        if (rtn <= 0){
            throw new WalletException(ErrorEnum.ERR_DB_UPDATE_DATA_ERR);
        }
    }
}
