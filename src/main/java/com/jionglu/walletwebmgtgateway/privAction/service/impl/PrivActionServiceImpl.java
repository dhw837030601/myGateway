package com.jionglu.walletwebmgtgateway.privAction.service.impl;


import com.jionglu.common.entity.sys.PrivAction;
import com.jionglu.common.exception.WalletException;
import com.jionglu.walletwebmgtgateway.mapper.action.PrivActionMapper;
import com.jionglu.walletwebmgtgateway.privAction.service.PrivActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("")
public class PrivActionServiceImpl implements PrivActionService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private PrivActionMapper privActionMapper;

	@Override
	public PrivAction getPrivActionByAction(String action) throws WalletException {
		return privActionMapper.getPrivActionByAction(action);
	}
}
