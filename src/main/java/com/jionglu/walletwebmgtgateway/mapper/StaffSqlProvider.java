package com.jionglu.walletwebmgtgateway.mapper;


import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class StaffSqlProvider {
	
	public String getActionListByStaffIdAndCategory(Map<String, Object> parameter){

		final Integer staffId = (parameter != null) ? (Integer) parameter.get("staffId") : null;

		final Integer category = (parameter != null) ? (Integer) parameter.get("category") : null;

		final Integer roleId = (parameter != null) ? (Integer) parameter.get("roleId") : null;

		String sql = new SQL(){{

			SELECT(" DISTINCT a.action ");

			FROM(" priv_action_tbl a  ");

			LEFT_OUTER_JOIN(" priv_role_action ra on a.id=ra.actionId ");

			/*LEFT_OUTER_JOIN(" staff sa on sa.roleId=ra.roleId ");*/

			if (roleId == null && (staffId != null && !staffId.equals(-1) && category != null && !category.equals(-1))) {

				WHERE(" ra.roleId in (select roleId from priv_user_role_tbl p where p.userId=#{staffId} and p.category=#{category})");

			}

			if (roleId != null){
				WHERE(" ra.roleId = #{roleId}");
			}

			if (category != null){
				WHERE(" a.category = #{category}");
			}


		}}.toString();
		
		return sql;
		
	}
	
}
