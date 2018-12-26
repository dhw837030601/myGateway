package com.jionglu.walletwebmgtgateway.staff.service.impl;

import com.jionglu.common.entity.staff.Staff;
import com.jionglu.common.entity.sys.PrivAction;
import com.jionglu.walletwebmgtgateway.mapper.StaffMapper;
import com.jionglu.walletwebmgtgateway.staff.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 段华微
 * @version V1.0
 * @description 描述
 * @ClassName: StaffServiceImpl
 * @Date 2018/6/11 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffMapper staffMapper;

    @Override
    public Staff getStaffById(Integer staffId) {
        return staffMapper.selectByPrimaryKey(staffId);
    }

    @Override
    public List<PrivAction> getActionListByStaffIdAndCategory(Integer staffId,Integer category,Integer roleId) {
        return staffMapper.getActionListByStaffIdAndCategory(staffId,category,roleId);
    }
}
