package com.baoxian.common.interceptor;

import com.baoxian.common.util.SlaveUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * @author 李德英
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class SlaveInterceptor implements Interceptor {
	private static Logger logger = LoggerFactory.getLogger(SlaveInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MetaObject handler = SystemMetaObject.forObject(statementHandler);
		while (!handler.hasGetter("delegate") && handler.hasGetter("h.target")) {
			statementHandler = (StatementHandler) handler.getValue("h.target");
			handler = SystemMetaObject.forObject(statementHandler);
		}
		if (!handler.hasGetter("delegate.boundSql")) {
			logger.warn("未获取到BoundSql对象");
			return invocation.proceed();
		}

		BoundSql boundSql = (BoundSql) handler.getValue("delegate.boundSql");
		String srcSql = boundSql.getSql().trim();
		if (SlaveUtil.getSlave() != null && SlaveUtil.getSlave() && "SELECT".equalsIgnoreCase(srcSql.trim().substring(0, 6))) {
			srcSql = "/*slave*/" + srcSql;
			handler.setValue("delegate.boundSql.sql", srcSql);
			SlaveUtil.clear();
		}
		return invocation.proceed();
	}
}
