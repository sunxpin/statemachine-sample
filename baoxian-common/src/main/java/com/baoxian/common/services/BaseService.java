//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baoxian.common.services;

import com.baoxian.common.bean.Page;
import com.baoxian.common.bean.RowSet;
import com.baoxian.common.dao.BaseDao;
import com.baoxian.common.entity.BaseEntity;
import com.baoxian.common.plugin.SqlBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseService<T extends BaseEntity<?>> {

    @Autowired
    protected BaseDao<T> baseDao;

    public BaseService() {
    }

    public int insert(T t) {
        return t == null ? 0 : this.baseDao.insert(t);
    }

    public int insertByBatch(List<T> list) {
        return list != null && list.size() != 0 ? this.baseDao.insertByBatch(list) : 0;
    }

    public T queryById(String id) {
        return id == null ? null : this.baseDao.queryById(id);
    }

    public List<T> queryByIds(List<String> list) {
        return (List)(list != null && list.size() != 0 ? this.baseDao.queryByIds(list) : new ArrayList());
    }

    public int updateById(T t) {
        return t == null ? 0 : this.baseDao.updateById(t);
    }

    public int updateByIds(T t, List<String> list) {
        return t != null && list != null && list.size() != 0 ? this.baseDao.updateByIds(t, list) : 0;
    }

    public int deleteById(String id) {
        return id == null ? 0 : this.baseDao.deleteById(id);
    }

    public int deleteByIds(List<String> ids) {
        return ids != null && ids.size() != 0 ? this.baseDao.deleteByIds(ids) : 0;
    }

    public int deleteByWhere(String where, Object obj) {
        return this.baseDao.deleteByWhere(where, obj);
    }

    public int deleteByObj(T t) {
        return t == null ? 0 : this.baseDao.delete(t);
    }

    public List<Map<String, Object>> queryBySql(String sql, Map<String, Object> map) {
        return this.baseDao.queryBySql(sql, map);
    }

    public String queryBySqlToString(String sql, Map<String, Object> map) {
        return this.baseDao.queryBySqlToString(sql, map);
    }

    public int updateBySql(String sql, Map<String, Object> map) {
        return this.baseDao.updateBySql(sql, map);
    }

    public int deleteBySql(String sql, Map<String, Object> map) {
        return this.baseDao.deleteBySql(sql, map);
    }

    public int insertOrUpdate(T entity, List<String> fields) {
        return entity == null ? 0 : this.baseDao.insertOrUpdate(entity, fields);
    }

    public int insertOrUpdateAll(T entity, List<String> fields) {
        return entity == null ? 0 : this.baseDao.insertOrUpdateAll(entity, fields);
    }

    public T queryOne(Object obj) {
        return obj == null ? null : this.baseDao.queryOne(obj);
    }

    public List<T> query(Object obj) {
        return (List)(obj == null ? new ArrayList() : this.baseDao.query(obj));
    }

    public List<T> queryByPage(Object obj, Page page) {
        return this.baseDao.queryByPage(obj, page);
    }

    public List<T> queryByWhere(String where, Object obj) {
        return this.baseDao.queryByWhere(where, obj);
    }

    public List<T> queryByWhere(String where, Object obj, Page page) {
        return this.baseDao.queryByWhere(where, obj, page);
    }

    public List<T> queryByAppender(Object entity, String appender) {
        return this.baseDao.queryByAppender(entity, appender);
    }

    public List<T> queryByAppender(Object entity, String appender, Page page) {
        return this.baseDao.queryByAppender(entity, appender, page);
    }

    public List<T> queryBySqlBuilder(SqlBuilder sqlBuilder) {
        return this.baseDao.queryBySqlBuilder(sqlBuilder);
    }

    public int count(Object obj) {
        return this.baseDao.count(obj);
    }

    public int countByWhere(String where, Object obj) {
        return this.baseDao.countByWhere(where, obj);
    }

    public int countBySqlBuilder(SqlBuilder sqlBuilder) {
        return this.baseDao.countBySqlBuilder(sqlBuilder);
    }

    public int updateById(Object obj) {
        return obj == null ? 0 : this.baseDao.updateById(obj);
    }

    public int updateAllById(Object obj) {
        return obj == null ? 0 : this.baseDao.updateAllById(obj);
    }

    public int updateByIds(Object obj, List<String> list) {
        return obj == null ? 0 : this.baseDao.updateByIds(obj, list);
    }

    public int updateByWhere(Object entity, String where, Object obj) {
        return this.baseDao.updateByWhere(entity, where, obj);
    }

    public int updateAllByIds(Object obj, List<String> list) {
        return obj == null ? 0 : this.baseDao.updateAllByIds(obj, list);
    }

    public int updateAllByWhere(Object entity, String where, Object obj) {
        return this.baseDao.updateAllByWhere(entity, where, obj);
    }

    public int update(Object entity, Object obj) {
        return entity == null ? 0 : this.baseDao.update(entity, obj);
    }

    public int updateAll(Object entity, Object obj) {
        return entity == null ? 0 : this.baseDao.updateAll(entity, obj);
    }

    public int updateBySqlBuilder(Object obj, SqlBuilder sqlBuilder) {
        return obj == null ? 0 : this.baseDao.updateBySqlBuilder(obj, sqlBuilder);
    }

    public int updateAllBySqlBuilder(Object obj, SqlBuilder sqlBuilder) {
        return obj == null ? 0 : this.baseDao.updateAllBySqlBuilder(obj, sqlBuilder);
    }

    public int delete(Object obj) {
        return obj == null ? 0 : this.baseDao.delete(obj);
    }

    public int deleteBySqlBuilder(SqlBuilder sqlBuilder) {
        return this.baseDao.deleteBySqlBuilder(sqlBuilder);
    }

    public List<Map<String, Object>> queryBySql(String sql, Object obj) {
        return this.baseDao.queryBySql(sql, obj);
    }

    public List<Map<String, Object>> queryBySql(String sql, Object obj, Page page) {
        return this.baseDao.queryBySql(sql, obj, page);
    }

    public Map<String, Object> queryOneBySql(String sql, Object obj) {
        return this.baseDao.queryOneBySql(sql, obj);
    }

    public RowSet queryBySqlToRowSet(String sql, Object obj) {
        return this.baseDao.queryBySqlToRowSet(sql, obj);
    }

    public RowSet queryBySqlToRowSet(String sql, Object obj, Page page) {
        return this.baseDao.queryBySqlToRowSet(sql, obj, page);
    }

    public RowSet queryOneBySqlToRowSet(String sql, Object obj) {
        return this.baseDao.queryOneBySqlToRowSet(sql, obj);
    }

    public <S> List<S> queryBySqlToBean(String sql, Object obj, Class<S> clz) {
        return this.baseDao.queryBySqlToBean(sql, obj, clz);
    }

    public <S> List<S> queryBySqlToBean(String sql, Object obj, Class<S> clz, Page page) {
        return this.baseDao.queryBySqlToBean(sql, obj, clz, page);
    }

    public <S> S queryOneBySqlToBean(String sql, Object obj, Class<S> clz) {
        return this.baseDao.queryOneBySqlToBean(sql, obj, clz);
    }

    public String queryBySqlToString(String sql, Object obj) {
        return this.baseDao.queryBySqlToString(sql, obj);
    }

    public Integer queryBySqlToInteger(String sql, Object obj) {
        return this.baseDao.queryBySqlToInteger(sql, obj);
    }

    public Double queryBySqlToDouble(String sql, Object obj) {
        return this.baseDao.queryBySqlToDouble(sql, obj);
    }

    public int updateBySql(String sql, Object obj) {
        return this.baseDao.updateBySql(sql, obj);
    }

    public int deleteBySql(String sql, Object obj) {
        return this.baseDao.deleteBySql(sql, obj);
    }
}
