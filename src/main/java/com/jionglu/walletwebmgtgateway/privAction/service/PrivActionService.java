package com.jionglu.walletwebmgtgateway.privAction.service;


import com.jionglu.common.entity.sys.PrivAction;
import com.jionglu.common.exception.WalletException;
import com.jionglu.common.page.Pagination;
import com.jionglu.common.page.PaginationResult;

import java.util.HashMap;
import java.util.List;


public interface PrivActionService {
	/**
	 * 
	 * @description 根据Action路径查询后台Action信息
	 * @param action
	 * @return
	 * @throws WalletException
	 * @return PrivAction    返回类型 
	 * @author 梁凌
	 * @date 2016年9月2日 上午10:13:01
	 */
	PrivAction getPrivActionByAction(String action) throws WalletException;
}

