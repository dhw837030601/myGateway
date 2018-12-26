package com.jionglu.walletwebmgtgateway.mapper.action;


import com.jionglu.common.entity.sys.PrivAction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface PrivActionMapper extends Mapper<PrivAction> {

	/**
	 * 
	 * @description 根据条件查询后台Action信息
	 * @param param
	 * @return
	 * @return PaginationResult<PrivAction>    返回类型
	 * @author 梁凌
	 * @date 2016年9月2日 上午10:08:31
	 */
	@SelectProvider(type = PrivActionSqlProvider.class, method = "getPrivActionListByConditions")
	public List<PrivAction> getPrivActionListByConditions(Map<String, Object> param);
	
	/**
	 * 
	 * @description 根据Action路径查询后台Action信息
	 * @param action
	 * @return
	 * @return PrivAction    返回类型
	 * @author 梁凌
	 * @date 2016年9月2日 上午10:13:01
	 */
	@Select(" SELECT * FROM priv_action_tbl WHERE action = #{action}  and category=2")
	public PrivAction getPrivActionByAction(@Param("action") String action);
	
	/**
	 * 
	 * @description 查询指定Id之外是否还有该Action路径的Action信息
	 * @param action
	 * @param privActionId
	 * @return
	 * @return PrivAction    返回类型
	 * @author 梁凌
	 * @date 2016年9月2日 上午10:17:52
	 */
	@Select(" SELECT * FROM priv_action_tbl WHERE action = #{action} AND id != #{privActionId}  and category=2")
	public PrivAction queryByActionExceptId(@Param("action") String action, @Param("privActionId") Integer privActionId);
	
	/**
	 * 
	 * @description 获取所有Action信息
	 * @return
	 * @return List<PrivAction>    返回类型
	 * @author 梁凌
	 * @date 2016年9月22日 下午3:17:43
	 */
	@Select(" SELECT * FROM priv_action_tbl WHERE category = 2 ORDER BY directoryId ")
	public List<PrivAction> getAllActions();
	
	/**
	 * 
	 * @description 根据角色id获取action信息
	 * @return
	 * @return List<PrivAction>    返回类型
	 * @author 梁凌
	 * @date 2016年9月22日 下午5:58:11
	 */
	@Select(" SELECT p.* FROM priv_action_tbl p LEFT JOIN priv_role_action pra ON p.id = pra.actionId WHERE pra.roleId = #{PrivRoleId} AND p.category = #{category} ")
	public List<PrivAction> getActionsByRoleIdAndCategory(@Param("category") Integer category, @Param("PrivRoleId") Integer PrivRoleId);

	/**
	 *
	 * @description 根据角色id获取action信息
	 * @return
	 * @return List<PrivAction>    返回类型
	 * @author 梁凌
	 * @date 2016年9月22日 下午5:58:11
	 */
	@Select(" SELECT p.* FROM priv_action_tbl p LEFT JOIN priv_role_action pra ON p.id = pra.actionId WHERE pra.roleId = #{PrivRoleId} and p.category = 2")
	public List<PrivAction> getActionsByRId(@Param("PrivRoleId") Integer PrivRoleId);

	@Select(" SELECT * FROM priv_action_tbl WHERE action = #{action} and category=#{category} ")
    PrivAction getPrivActionByActionAndCategory(@Param("action") String action, @Param("category") Integer category);
}
