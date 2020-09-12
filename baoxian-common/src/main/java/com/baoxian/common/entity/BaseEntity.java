package com.baoxian.common.entity;

import com.baoxian.common.bean.Page;
import com.baoxian.common.bean.RowSet;
import com.baoxian.common.dao.BaseDao;
import com.baoxian.common.plugin.SqlBuilder;
import com.baoxian.common.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.beans.Introspector;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 李德英 继承此类的实体类并实例化模板参数，可以通过实体对象操作数据库
 */
public class BaseEntity<T> implements Serializable {

	private String id;

	private Date createDate;

	private Date modifyDate;

	public BaseEntity() {
		this.getClass().getClassLoader();
	}

	@Deprecated
	public boolean idIsNull() {
		return StringUtils.isBlank(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public T queryById() {
		return baseDao().queryById(this.getId().toString());
	}

	public boolean fill() {
		T t = null;
		if (StringUtils.isNotBlank(this.getId().toString())) {
			t = queryById();
		} else {
			t = queryOne();
		}
		if (t == null)
			return false;
		BeanUtils.copyProperties(t, this);
		return true;
	}

	@SuppressWarnings("unchecked")
	public int insert() {
		return baseDao().insert((T) this);
	}

	public int insertByBatch(List<T> list) {
		return baseDao().insertByBatch(list);
	}

	@SuppressWarnings("unchecked")
	public int insertOrUpdate(List<String> fields) {
		return baseDao().insertOrUpdate((T) this, fields);
	}

	@SuppressWarnings("unchecked")
	public int insertOrUpdateAll(List<String> fields) {
		return baseDao().insertOrUpdateAll((T) this, fields);
	}

	public List<T> queryByIds(List<String> list) {
		return baseDao().queryByIds(list);
	}

	public T queryOne() {
		return baseDao().queryOne(this);
	}

	public List<T> query() {
		return baseDao().query(this);
	}

	public List<T> queryByPage(Page page) {
		return baseDao().queryByPage(this, page);
	}

	public List<T> queryByAppender(String appender) {
		return baseDao().queryByAppender(this, appender);
	}

	public List<T> queryByAppender(Page page, String appender) {
		return baseDao().queryByAppender(this, appender, page);
	}

	public List<T> queryByWhere(String appender) {
		return baseDao().queryByWhere(appender, this);
	}

	public List<T> queryByWhere(Page page, String appender) {
		return baseDao().queryByWhere(appender, this, page);
	}

	public List<T> queryBySqlBuilder(SqlBuilder sqlBuilder) {
		return baseDao().queryBySqlBuilder(sqlBuilder);
	}

	public int count() {
		return baseDao().count(this);
	}

	public int countByWhere(String where) {
		return baseDao().countByWhere(where, this);
	}

	public int countBySqlBuilder(SqlBuilder sqlBuilder) {
		return baseDao().countBySqlBuilder(sqlBuilder);
	}

	public int updateById() {
		return baseDao().updateById(this);
	}

	public int updateAllById() {
		return baseDao().updateAllById(this);
	}

	public int updateByIds(List<String> list) {
		return baseDao().updateByIds(this, list);
	}

	public int updateByWhere(String where, Object obj) {
		return baseDao().updateByWhere(this, where, obj);
	}

	public int updateAllByIds(List<String> list) {
		return baseDao().updateAllByIds(this, list);
	}

	public int updateAllByWhere(String where, Object obj) {
		return baseDao().updateAllByWhere(this, where, obj);
	}

	public int update(Object obj) {
		return baseDao().update(this, obj);
	}

	public int updateAll(Object obj) {
		return baseDao().updateAll(this, obj);
	}

	public int updateBySqlBuilder(SqlBuilder sqlBuilder) {
		return baseDao().updateBySqlBuilder(this, sqlBuilder);
	}

	public int updateAllBySqlBuilder(SqlBuilder sqlBuilder) {
		return baseDao().updateAllBySqlBuilder(this, sqlBuilder);
	}

	public int delete() {
		return baseDao().delete(this);
	}

	public int deleteById() {
		return baseDao().deleteById(this.getId().toString());
	}

	public int deleteByIds(List<String> ids) {
		return baseDao().deleteByIds(ids);
	}

	public int deleteByWhere(String where) {
		return baseDao().deleteByWhere(where, this);
	}

	public int deleteBySqlBuilder(SqlBuilder sqlBuilder) {
		return baseDao().deleteBySqlBuilder(sqlBuilder);
	}

	public List<Map<String, Object>> queryBySql(String sql) {
		return baseDao().queryBySql(sql, this);
	}

	public List<Map<String, Object>> queryBySql(String sql, Page page) {
		return baseDao().queryBySql(sql, this, page);
	}

	public Map<String, Object> queryOneBySql(String sql) {
		return baseDao().queryOneBySql(sql, this);
	}

	public RowSet queryBySqlToRowSet(String sql) {
		return baseDao().queryBySqlToRowSet(sql, this);
	}

	public RowSet queryBySqlToRowSet(String sql, Page page) {
		return baseDao().queryBySqlToRowSet(sql, this, page);
	}

	public RowSet queryOneBySqlToRowSet(String sql) {
		return baseDao().queryOneBySqlToRowSet(sql, this);
	}

	public <S> List<S> queryBySqlToBean(String sql, Class<S> clz) {
		return baseDao().queryBySqlToBean(sql, this, clz);
	}

	public <S> List<S> queryBySqlToBean(String sql, Class<S> clz, Page page) {
		return baseDao().queryBySqlToBean(sql, this, clz, page);
	}

	public <S> S queryOneBySqlToBean(String sql, Class<S> clz) {
		return baseDao().queryOneBySqlToBean(sql, this, clz);
	}

	public String queryBySqlToString(String sql) {
		return baseDao().queryBySqlToString(sql, this);
	}

	public Integer queryBySqlToInteger(String sql) {
		return baseDao().queryBySqlToInteger(sql, this);
	}

	public Double queryBySqlToDouble(String sql) {
		return baseDao().queryBySqlToDouble(sql, this);
	}

	public int updateBySql(String sql) {
		return baseDao().updateBySql(sql, this);
	}

	public int deleteBySql(String sql) {
		return baseDao().deleteBySql(sql, this);
	}

	@SuppressWarnings("unchecked")
	public BaseDao<T> baseDao() {
		String beanName = Introspector.decapitalize(this.getClass().getSimpleName() + "Dao");
		return (BaseDao<T>) SpringContextUtil.getBean(beanName);
	}

}
