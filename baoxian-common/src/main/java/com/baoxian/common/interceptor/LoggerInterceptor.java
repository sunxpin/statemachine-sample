package com.baoxian.common.interceptor;

import com.baoxian.common.util.NoLoggerSqlUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author 李德英
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "parameterize", args = { Statement.class }) })
public class LoggerInterceptor implements Interceptor {
	private static Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object obj = invocation.proceed();
		Boolean b = NoLoggerSqlUtil.getNoLogger();
		if(b != null){
			try {
				if(b) return obj;
			}finally {
				NoLoggerSqlUtil.clearNoLogger();
			}
		}
		PreparedStatement preparedStatement = (PreparedStatement) invocation.getArgs()[0];
		String sql = String.valueOf(preparedStatement).replaceAll("[\r\n]", " ").trim();
		logger.info(sql.substring(sql.indexOf(": ") + 2));
		return obj;
	}
}
