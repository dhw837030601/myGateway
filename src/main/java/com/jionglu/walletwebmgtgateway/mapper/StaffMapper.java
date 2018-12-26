package com.jionglu.walletwebmgtgateway.mapper;

import com.jionglu.common.entity.staff.Staff;
import com.jionglu.common.entity.sys.PrivAction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 段华微
 * @version V1.0
 * @description 描述
 * @ClassName: StaffMapper
 * @Date 2018/6/11 Copyright(c) 2015 www.wallet.com All rights
 * reserved
 */
public interface StaffMapper extends Mapper<Staff> {

    @SelectProvider(type = StaffSqlProvider.class, method = "getActionListByStaffIdAndCategory")
    List<PrivAction> getActionListByStaffIdAndCategory(@Param("staffId") Integer staffId,@Param("category") Integer category,@Param("roleId") Integer roleId);
}
