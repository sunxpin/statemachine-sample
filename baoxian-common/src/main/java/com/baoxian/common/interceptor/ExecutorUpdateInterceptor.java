package com.baoxian.common.interceptor;

import com.baoxian.common.entity.BaseEntity;
import com.baoxian.common.plugin.SqlBuilder;
import com.baoxian.common.util.UUIDTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author 李德英
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class ExecutorUpdateInterceptor implements Interceptor {
	private static Logger logger = LoggerFactory.getLogger(ExecutorUpdateInterceptor.class);

	@SuppressWarnings({ "unchecked" })
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = invocation.getArgs()[1];
		if (mappedStatement.getId().endsWith(".updateBySql")
				|| mappedStatement.getId().endsWith(".deleteBySql")) {
			ParamMap<Object> paramMap = (ParamMap<Object>) parameter;
			String sql = (String) paramMap.get("sql");
			paramMap.put("sql", sql.replaceAll("#\\{", "#{obj."));
		} else if (mappedStatement.getId().endsWith(".deleteByWhere")
				|| mappedStatement.getId().endsWith(".updateByWhere")
				|| mappedStatement.getId().endsWith(".updateAllByWhere")) {
			ParamMap<Object> paramMap = (ParamMap<Object>) parameter;
			String sql = (String) paramMap.get("where");
			paramMap.put("where", sql.replaceAll("#\\{", "#{obj."));
		} else if (mappedStatement.getId().endsWith(".deleteBySqlBuilder")) {
			SqlBuilder sqlBuilder = (SqlBuilder) parameter;
			sqlBuilder.setSql(" WHERE " + sqlBuilder.getBuilder().replaceAll("#\\{", "#{map."));
		} else if (mappedStatement.getId().endsWith(".updateBySqlBuilder") 
				|| mappedStatement.getId().endsWith(".updateAllBySqlBuilder")) {
			ParamMap<Object> map = (ParamMap<Object>) parameter;
			SqlBuilder sqlBuilder = (SqlBuilder) map.get("sqlBuilder");
			sqlBuilder.setSql(" WHERE " + sqlBuilder.getBuilder().replaceAll("#\\{", "#{sqlBuilder.map."));
		} else if (mappedStatement.getId().contains(".insert")) {
			if (parameter instanceof Map) {
				for (Object v : ((Map<?, ?>) parameter).values()) {
					if (v instanceof List) {
						for (Object e : (List<?>) v)
							setBaseEntityId(e);
					} else {
						setBaseEntityId(v);
					}
				}
			} else {
				setBaseEntityId(parameter);
			}
		}
		long s = System.currentTimeMillis();
		Object obj = invocation.proceed();
		long e = System.currentTimeMillis();
		logger.info("更新耗时: " + (e - s) + "ms, 更新记录数: " + obj + "条");
		return obj;
	}

	private void setBaseEntityId(Object obj) {
		if (!(obj instanceof BaseEntity))
			return;
		BaseEntity<?> entity = (BaseEntity<?>) obj;
		if (StringUtils.isBlank(entity.getId()))
			entity.setId(UUIDTools.getUUID());
	}
}
