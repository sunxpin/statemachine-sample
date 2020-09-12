package com.baoxian.common.interceptor;

import com.baoxian.common.bean.Page;
import com.baoxian.common.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * @author 李德英
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }) })
public class PageInterceptor implements Interceptor {
	private static Logger logger = LoggerFactory.getLogger(PageInterceptor.class);
	public Object intercept(Invocation invocation) throws Throwable {

		// 当前环境 MappedStatement，BoundSql，及sql取得
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = invocation.getArgs()[1];
		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		String originalSql = boundSql.getSql().trim();
		Object parameterObject = boundSql.getParameterObject();

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

		if (page == null)
			page = PageUtil.getPage();

		if (page != null) { // Page对象存在的场合，开始分页处理
			try {
				if (page.getTotalRecords() < 0) { // 如果总数小于0则统计总记录数
					String countSql = getCountSql(originalSql);
					Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource()
							.getConnection();
					PreparedStatement countStmt = connection.prepareStatement(countSql);
					BoundSql countBS = copyFromBoundSql(mappedStatement, boundSql, countSql);
					DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement,
							parameterObject, countBS);
					parameterHandler.setParameters(countStmt);
					String sql = String.valueOf(countStmt).replaceAll("[\r\n]", "").trim();
					logger.info(sql.substring(sql.indexOf(": ") + 2));
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

				// 对原始Sql追加limit
				String newSql = originalSql + " LIMIT " + page.getStartRecord() + ", " + page.getPageRecords();
				BoundSql newBoundSql = copyFromBoundSql(mappedStatement, boundSql, newSql);
				MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
				invocation.getArgs()[0] = newMs;

			} finally {
				PageUtil.clearPage();
			}
		}

		return invocation.proceed();

	}

	/**
	 * 复制MappedStatement对象
	 */
	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		builder.keyProperty(StringUtils.join(ms.getKeyProperties(), ","));
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	/**
	 * 复制BoundSql对象
	 */
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

	/**
	 * 根据原Sql语句获取对应的查询总记录数的Sql语句
	 */
	private String getCountSql(String sql) {
		if(sql.substring(0, 9).equalsIgnoreCase("/*slave*/"))
			return "/*slave*/SELECT COUNT(*) FROM (" + sql.substring(9) + ") __t";
		else
			return "SELECT COUNT(*) FROM (" + sql + ") __t";
	}

	public class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}
}
