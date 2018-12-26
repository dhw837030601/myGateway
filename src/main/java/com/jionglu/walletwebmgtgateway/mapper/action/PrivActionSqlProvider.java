package com.jionglu.walletwebmgtgateway.mapper.action;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class PrivActionSqlProvider {
	
	public String getPrivActionListByConditions(Map<String, Object> parameter){
		
		final Integer directoryId = (parameter != null) ? (Integer) parameter.get("directoryId") : null;
		
		final String action = (parameter != null) ? (String) parameter.get("action") : null;

		final String actionName = (parameter != null) ? (String) parameter.get("actionName") : null;
		
		String sql = new SQL(){{
			
			SELECT(" pat.*,d.directoryName AS directoryName ");
			
			FROM(" priv_action_tbl pat ");
			
			LEFT_OUTER_JOIN(" `directory` d ON pat.directoryId = d.id ");
			
			if (directoryId != null && !directoryId.equals(-1)) {
				
				WHERE(" pat.directoryId = #{directoryId} ");
			
			}
			
			if (action != null && !StringUtils.isEmpty(action)) {
				
				WHERE(" pat.action like CONCAT('%',#{action},'%') ");
			
			}

			if (actionName != null && !StringUtils.isEmpty(actionName)) {

				WHERE(" pat.actionName like CONCAT('%',#{actionName},'%') ");

			}
			WHERE("   pat.category=2 ");
		}}.toString();
		
		return sql;
		
	}
	
}
