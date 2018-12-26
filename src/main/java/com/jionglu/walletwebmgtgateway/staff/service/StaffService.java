package com.jionglu.walletwebmgtgateway.staff.service;

import com.jionglu.common.entity.staff.Staff;
import com.jionglu.common.entity.sys.PrivAction;

import java.util.List;

/**
 * @author 段华微
 * @version V1.0
 * @description 描述
 * @ClassName: StaffService
 * @Date 2018/6/11 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
public interface StaffService {

    public Staff getStaffById(Integer staffId);

    public List<PrivAction> getActionListByStaffIdAndCategory(Integer staffId,Integer category,Integer roleId);
}
