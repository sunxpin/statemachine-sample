package com.baoxian.common.interceptor;

import com.baoxian.common.bean.RowSet;
import com.baoxian.common.plugin.SqlBuilder;
import com.baoxian.common.util.CastUtil;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author 李德英
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }) })
public class ExecutorQueryInterceptor implements Interceptor {
	private static Logger logger = LoggerFactory.getLogger(ExecutorQueryInterceptor.class);

	@SuppressWarnings("unchecked")
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = invocation.getArgs()[1];
		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		Object parameterObject = boundSql.getParameterObject();
		if (mappedStatement.getId().endsWith(".queryBySqlBuilder") 
				|| mappedStatement.getId().endsWith(".countBySqlBuilder")
				|| mappedStatement.getId().endsWith(".queryOneBySqlBuilder")) {
			SqlBuilder sqlBuilder = (SqlBuilder) parameter;
			sqlBuilder.setSql(sqlBuilder.getBuilder().replaceAll("#\\{", "#{map."));
		} else if (mappedStatement.getId().endsWith(".queryByWhere")
				|| mappedStatement.getId().endsWith(".countByWhere")) {	
			ParamMap<Object> paramMap = (ParamMap<Object>) parameter;
			String sql = (String) paramMap.get("where");
			paramMap.put("where", sql.replaceAll("#\\{", "#{obj."));
		} else if (mappedStatement.getId().contains(".queryBySql") 
				|| mappedStatement.getId().contains(".queryOneBySql")) {
			ParamMap<Object> paramMap = (ParamMap<Object>) parameter;
			String sql = (String) paramMap.get("sql");
			paramMap.put("sql", sql.replaceAll("#\\{", "#{obj."));
		}

		long s = System.currentTimeMillis();
		Object result = invocation.proceed();
		long e = System.currentTimeMillis();
		logger.info("查询耗时: " + (e - s) + "ms, 返回记录数: " + ((List<?>) result).size() + "条");
		
		if (mappedStatement.getId().endsWith("ToBean")) {
			result = trans2BeanList(parameterObject, (List<Map<String, Object>>)result);
		} else if (mappedStatement.getId().endsWith("ToRowSet")){
			result = Arrays.asList(new RowSet((List<Map<String, Object>>)result));
		}
		return result;
	}
	
	
	private Object trans2BeanList(Object parameterObject, List<Map<String, Object>> result) {
		Class<?> resultClz = null;	
		for (Object obj : ((HashMap<?, ?>) parameterObject).values()) {
			if (obj instanceof Class) {
				resultClz = (Class<?>) obj;
				break;
			}
		}
		List<Object> resultList = new ArrayList<>(result.size());
		for(Map<String, Object> ele : result) {
			resultList.add(transOneBean( ele, resultClz));
		}
		return resultList;
	}
	
	private Object transOneBean(Map<String, Object> map, Class<?> resultClz) {
		Object oneResult;
		if(CastUtil.isPrimitiveType(resultClz)) {
			if(map != null && map.size() > 0) {
				return CastUtil.cast(map.values().toArray()[0], resultClz);
			}
			return null;
		}
		
		try {
			oneResult = resultClz.newInstance();
		} catch (Exception e) {
			logger.error("创建实例对象失败: " + resultClz.getName());
			return null;
		}
		
		if(map == null) return oneResult;
		MetaObject handler = SystemMetaObject.forObject(oneResult);
		map.forEach((k, v) -> {
			String property = handler.findProperty(k, false);
			if(property != null && v != null && handler.hasSetter(property))
				handler.setValue(property, CastUtil.cast(v, handler.getGetterType(property)));
		});
		return oneResult;
	}
}
