package com.baoxian.common.plugin;

import com.baoxian.common.bean.Page;
import com.baoxian.common.util.PageUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李德英
 */
public class SqlBuilder {
	private Map<String, Object> map = new HashMap<>();
	private StringBuilder builder = new StringBuilder();
	private String sql;
	private int serino = 1;

	public String getSql() {
		if(sql == null)
			sql = builder.toString();
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setBuilder(String condition) {
		this.builder = new StringBuilder(condition);
	}

	public SqlBuilder setMap(Map<String, Object> map) {
		if (map != null)
			this.map = map;
		return this;
	}

	public String getBuilder() {
		return builder.toString();
	}

	public SqlBuilder select(String fields) {
		builder.append("SELECT ");
		if (StringUtils.isBlank(fields))
			builder.append("*");
		else
			builder.append(fields);
		builder.append(" ");
		return this;
	}

	public SqlBuilder select() {
		builder.append("SELECT * ");
		return this;
	}

	public SqlBuilder from(String table) {
		builder.append("FROM " + table + " WHERE ");
		return this;
	}

	public SqlBuilder where(String condition) {
		return this.append(condition, null);
	}

	public SqlBuilder where(String condition, Map<String, ?> map) {
		return this.append(condition, map);
	}

	public SqlBuilder append(String condition) {
		andIfNeed();
		this.builder.append(condition);
		return this;
	}

	public SqlBuilder append(String condition, Map<String, ?> map) {
		andIfNeed();
		this.builder.append(condition);
		if (map != null)
			this.map.putAll(map);
		return this;
	}

	public SqlBuilder eq(String field, Object fieldVal) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " = #{" + param + "}");
		map.put(param, fieldVal);
		return this;
	}

	public SqlBuilder neq(String field, Object fieldVal) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " != #{" + param + "}");
		map.put(param, fieldVal);
		return this;
	}

	public SqlBuilder or() {
		builder.append(" OR ");
		return this;
	}

	public SqlBuilder and() {
		builder.append(" AND ");
		return this;
	}

	public SqlBuilder bewteen(String field, Object value1, Object value2) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " BEWTEEN #{s_" + param + "} AND #{e_" + param + "}");
		map.put("s_" + param, value1);
		map.put("e_" + param, value2);
		return this;
	}

	public SqlBuilder lt(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " < #{" + param + "}");
		map.put(param, value);
		return this;
	}

	public SqlBuilder gt(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " > #{" + param + "}");
		map.put(param, value);
		return this;
	}

	public SqlBuilder le(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " <= #{" + param + "}");
		map.put(param, value);
		return this;
	}

	public SqlBuilder ge(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " >= #{" + param + "}");
		map.put(param, value);
		return this;
	}

	public SqlBuilder ne(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " != #{" + param + "}");
		map.put(param, value);
		return this;
	}

	public SqlBuilder like(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " LIKE CONCAT('%', #{" + param + "}, '%')");
		map.put(param, value);
		return this;
	}

	public SqlBuilder llike(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " LIKE CONCAT(#{" + param + "}, '%')");
		map.put(param, value);
		return this;
	}

	public SqlBuilder rlike(String field, Object value) {
		andIfNeed();
		String param = field + "_" + serino++;
		builder.append(field + " LIKE CONCAT('%', #{" + param + "})");
		map.put(param, value);
		return this;
	}

	public SqlBuilder isNull(String field) {
		andIfNeed();
		builder.append(field + " IS NULL");
		return this;
	}

	public SqlBuilder isNotNull(String field) {
		andIfNeed();
		builder.append(field + " IS NOT NULL");
		return this;
	}

	public SqlBuilder in(String field, List<?> inList) {
		andIfNeed();
		builder.append(field + " IN (");
		for (int i = 0; i < inList.size(); i++) {
			String param = field + "_" + serino++;
			builder.append((i > 0 ? "," : "") + "#{" + param + "}");
			map.put(param, inList.get(i));
		}
		builder.append(")");
		return this;
	}

	public SqlBuilder notIn(String field, List<?> inList) {
		andIfNeed();
		builder.append(field + " NOT IN (");
		for (int i = 0; i < inList.size(); i++) {
			String param = field + "_" + serino++;
			builder.append((i > 0 ? "," : "") + "#{" + param + "} ");
			map.put(param, inList.get(i));
		}
		builder.append(")");
		return this;
	}

	public SqlBuilder startBracket() {
		andIfNeed();
		builder.append(" ( ");
		return this;
	}
	
	public SqlBuilder endBracket() {
		builder.append(" ) ");
		return this;
	}
	
	public SqlBuilder bracket(String condition) {
		andIfNeed();
		builder.append(" ( ");
		builder.append(condition);
		builder.append(" ) ");
		return this;
	}

	public SqlBuilder limit(int start, int records) {
		builder.append(" LIMIT #{limit_start}, #{limit_records}");
		map.put("limit_start", start);
		map.put("limit_records", records);
		return this;
	}

	public SqlBuilder orderBy(String field, String order) {
		builder.append(" ORDER BY " + field + " #{order_" + field + "}");
		map.put("order_" + field, order);
		return this;
	}

	public SqlBuilder groupBy(String fields) {
		builder.append(" GROUP BY " + fields);
		return this;
	}

	public SqlBuilder having(String cond) {
		builder.append(" HAVING " + cond);
		return this;
	}
	
	public SqlBuilder setPage(Page page) {
		PageUtil.setPage(page);
		return this;
	}

	public String toSql() {
		return builder.toString();
	}

	public Map<String, Object> getParamMap() {
		return map;
	}

	public Map<String, Object> getParamMap(Map<String, ?> map) {
		if (map != null)
			this.map.putAll(map);
		return this.map;
	}

	private void andIfNeed() {
		if (StringUtils.isBlank(builder.toString()) || builder.toString().matches(".*(WHERE|\\()\\s*$"))
			return;
		if (!builder.toString().matches(".*(AND|OR)\\s*$"))
			builder.append(" AND ");
	}

}
