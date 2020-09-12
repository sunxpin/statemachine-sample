package com.baoxian.common.interceptor;

import com.baoxian.common.bean.Page;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;

/**
 * @Intercepts 说明是一个拦截器
 * @Signature 拦截器的签名 type 拦截的类型 四大对象之一(
 *            Executor,ResultSetHandler,ParameterHandler,StatementHandler)
 *            method 拦截的方法 args 参数,高版本需要加个Integer.class参数,不然会报错
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class PageInterceptor2 implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		// 获取StatementHandler，默认是RoutingStatementHandler
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		// 获取statementHandler包装类
		MetaObject metaObjectHandler = SystemMetaObject.forObject(statementHandler);
		// 获取进行数据库操作时管理参数的handler
		// ParameterHandler parameterHandler = (ParameterHandler)
		// metaObjectHandler.getValue("delegate.parameterHandler");
		// 获取请求时的参数
		// Map<String, Object> paraObject = (Map<String, Object>)
		// parameterHandler.getParameterObject();
		// 获取连接对象
		// Connection connection = (Connection) invocation.getArgs()[0];
		// object.getValue("delegate"); 获取StatementHandler的实现类
		// 也可以这样获取
		// paraObject = (Map<String, Object>)
		// statementHandler.getBoundSql().getParameterObject();
		// 分离代理对象链
		while (metaObjectHandler.hasGetter("h")) {
			Object obj = metaObjectHandler.getValue("h");
			metaObjectHandler = SystemMetaObject.forObject(obj);
		}

		while (metaObjectHandler.hasGetter("target")) {
			Object obj = metaObjectHandler.getValue("target");
			metaObjectHandler = SystemMetaObject.forObject(obj);
		}

		// 获取查询接口映射的相关信息
		MappedStatement mappedStatement = (MappedStatement) metaObjectHandler.getValue("delegate.mappedStatement");
		// String mapId = mappedStatement.getId();

		// statementHandler.getBoundSql().getParameterObject();
		Object parameterObject = statementHandler.getBoundSql().getParameterObject();

		// Page对象获取
		Page page = null;
		if (parameterObject instanceof HashMap) {
			for (Object obj : ((HashMap<?, ?>) parameterObject).values()) {
				if (obj instanceof Page) {
					page = (Page) obj;
					break;
				}
			}
		} else if (parameterObject instanceof Page) {
			page = (Page) parameterObject;
		}

		if (page != null) {
			BoundSql boundSql = (BoundSql) metaObjectHandler.getValue("delegate.boundSql");
			String sql = boundSql.getSql().trim(); // (String)
													// MetaObjectHandler.getValue("delegate.boundSql.sql");
			if (page.getTotalRecords() < 0) {
				String countSql = getCountSql(sql);
				Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource()
						.getConnection();
				PreparedStatement countStmt = connection.prepareStatement(countSql);
				BoundSql countBS = copyFromBoundSql(mappedStatement, boundSql, countSql);
				DefaultParameterHandler parameterHandler2 = new DefaultParameterHandler(mappedStatement,
						parameterObject, countBS);
				parameterHandler2.setParameters(countStmt);
				ResultSet rs = countStmt.executeQuery();
				int totalRecords = 0;
				if (rs.next()) {
					totalRecords = rs.getInt(1);
				}
				rs.close();
				countStmt.close();
				connection.close();
				// 分页计算
				page.setTotalRecords(totalRecords);
			}
			String limitSql = sql + " limit " + page.getStartRecord() + "," + page.getPageRecords();

			// 将构建完成的分页sql语句赋值个体'delegate.boundSql.sql'，偷天换日
			metaObjectHandler.setValue("delegate.boundSql.sql", limitSql);
		}

		// 调用原对象的方法，进入责任链的下一级
		return invocation.proceed();
	}

	// 获取代理对象
	public Object plugin(Object o) {
		// 生成object对象的动态代理对象
		return Plugin.wrap(o, this);
	}

	// 设置代理对象的参数
	public void setProperties(Properties properties) {
	}

	private String getCountSql(String sql) {
		return "SELECT COUNT(*) FROM (" + sql + ") aliasForPage";
	}

	private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(),
				boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
			}
		}
		return newBoundSql;
	}
}