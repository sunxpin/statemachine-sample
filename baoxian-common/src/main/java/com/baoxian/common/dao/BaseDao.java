package com.baoxian.common.dao;

import com.baoxian.common.bean.Page;
import com.baoxian.common.bean.RowSet;
import com.baoxian.common.entity.BaseEntity;
import com.baoxian.common.plugin.SqlBuilder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 李德英
 * Dao基础接口, 所有需要实现Dao功能的接口都强烈建议继承此接口, 以减少Dao编码工作。<br>
 * 方法名中包含BySql的均为公共方法, 其余方法跟具体的继承接口相关。<br>
 * 此接口包含常用的增、删、改、查功能, 如有特殊需求可按原始方式重写覆盖即可。<br>
 *
 * @param <T> 模板参数必须继承自{@link BaseEntity}
 */

@Mapper
public interface BaseDao<T> {

	public int insert(T entity);

	public int insertByBatch(List<T> list);

	/**
	 * 依据field字段判断是否存在记录, 不存在则插入, 存在则更新, 仅更新属性不为null的字段
	 * @param entity
	 * @return 
	 */
	public int insertOrUpdate(T entity, List<String> fields);
	
	/**
	 * 依据field字段判断是否存在记录, 不存在则插入, 存在则更新, 属性为null也将会更新到数据库相应字段
	 * @param entity
	 * @return
	 */
	public int insertOrUpdateAll(T entity, List<String> fields);

	public T queryById(String id);

	public List<T> queryByIds(List<String> list);

	/**
	 * 根据条件查询一条数据, 为确保最多只返回一条数据, 执行时会自动加上LIMIT 1, 使用时请注意
	 * @param obj 可以是具体的实例对象, 也可以Map类
	 * @return
	 */
	public T queryOne(Object obj);

	/**
	 * 查询多条数据
	 * @param obj 可以是具体的实例对象, 也可以Map类
	 * @return
	 */
	public List<T> query(Object obj);

	/**
	 * 分页查询
	 * @param obj 可以是具体的实例对象, 也可以Map类
	 * @param page 分页对象
	 * @return
	 */
	public List<T> queryByPage(Object obj, Page page);
	
	/**
	 * queryByWhere("username = #{username} OR age > #{age} ORDER BY createDate", person);
	 * @param where where条件或其他, 不能为null
	 * @param obj where中需要用到的参数
	 * @return
	 */
	public List<T> queryByWhere(String where, Object obj);
	
	public List<T> queryByWhere(String where, Object obj, Page page);
	
	/**
	 * queryByAppender(person, " ORDER BY createDate");
	 * @param entity 查询条件
	 * @param appender 附加条件
	 * @return
	 */
	public List<T> queryByAppender(Object entity, String appender);
	
	public List<T> queryByAppender(Object entity, String appender, Page page);

	/**
	 * 通过SQLBuilder查询<br>
	 * queryBySqlBuilder(new SqlBuilder().eq("username", "admin").gt("age", 20));
	 * @param sqlBuilder 仅做为SQL语句中的WHERE条件
	 * @return
	 */
	public List<T> queryBySqlBuilder(SqlBuilder sqlBuilder);
	
	public T queryOneBySqlBuilder(SqlBuilder sqlBuilder);

	public int count(Object obj);
	
	public int countByWhere(String where, Object obj);

	public int countBySqlBuilder(SqlBuilder sqlBuilder);

	/**
	 * 仅更新属性不为null的字段
	 * @param obj 可以是实例对象, 也可以是Map
	 * @return
	 */
	public int updateById(Object obj);

	/**
	 * 更新所有字段, 包含属性为null的情况
	 * @param obj 可以是实例对象, 也可以是Map
	 * @return
	 */
	public int updateAllById(Object obj);

	/**
	 * 按id更新属性不为null的字段
	 * @param obj 需要更新的属性, id将忽略
	 * @param list
	 * @return
	 */
	public int updateByIds(Object obj, List<String> list);
	
	/**
	 * updateByWhere(person, "username = #{username} OR age > #{age}", map)
	 * @param entity 更新值
	 * @param where 更新条件语句, 不能为null
	 * @param obj where中需要用到的参数
	 * @return
	 */
	public int updateByWhere(Object entity, String where, Object obj);

	/**
	 * 按id更新所有字段, 包含属性为null的情况
	 * @param obj 需要更新的属性, id将忽略
	 * @param list id集合
	 * @return
	 */
	public int updateAllByIds(Object obj, List<String> list);
	
	public int updateAllByWhere(Object entity, String where, Object obj);

	/**
	 * 更新属性不为null的字段
	 * @param entity 需要更新的属性, id将忽略
	 * @param obj 更新条件, 所有属性之间是AND关系
	 * @return
	 */
	public int update(Object entity, Object obj);

	/**
	 * 更新所有字段, 包含属性为null的情况
	 * @param entity 需要更新的属性, id将忽略
	 * @param obj 更新条件, 所有属性之间是AND关系
	 * @return
	 */
	public int updateAll(Object entity, Object obj);

	/**
	 * 更新属性不为null的字段
	 * @param obj 需要更新的属性, id将忽略
	 * @param sqlBuilder 对应SQL语句中的WHERE条件
	 * @return 更新结果数
	 */
	public int updateBySqlBuilder(Object obj, SqlBuilder sqlBuilder);

	/**
	 * 更新所有字段, 包含属性为null的情况
	 * @param obj 需要更新的属性, id将忽略
	 * @param sqlBuilder 对应SQL语句中的WHERE条件
	 * @return
	 */
	public int updateAllBySqlBuilder(Object obj, SqlBuilder sqlBuilder);

	public int delete(Object obj);

	public int deleteById(String id);

	public int deleteByIds(List<String> ids);
	
	/**
	 * deleteByWhere("username = #{username} OR age > #{age}", map)
	 * @param where 删除条件语句, 不能为null
	 * @param obj where中需要用到的参数
	 * @return
	 */
	public int deleteByWhere(String where, Object obj);

	/**
	 * deleteBySqlBuilder(new SqlBuilder().eq("username", "admin").gt("age", 20));
	 * @param sqlBuilder 对应SQL语句中的WHERE条件
	 * @return
	 */
	public int deleteBySqlBuilder(SqlBuilder sqlBuilder);

	/**
	 * 示例: <br>
	 * queryBySql("SELECT * FROM member WHERE username = #{usermame}", member);<br>
	 * queryBySql("SELECT * FROM member WHERE username = #{usermame}", map);<br>
	 * queryBySql(sqlBuilder.getSql(), sqlBuilder.getParamMap());<br>
	 * @param sql MyBatis形式的SQL语句
	 * @param obj 参数, 可以是对象, 也可以是Map
	 * @return
	 */
	public List<Map<String, Object>> queryBySql(String sql, Object obj);

	/**
	 * 分页查询, 参考{@link BaseDao#queryBySql(String, Object)}
	 * @param sql MyBatis形式的SQL语句
	 * @param obj 参数, 可以是对象, 也可以是Map
	 * @param page 分页对象
	 * @return
	 */
	public List<Map<String, Object>> queryBySql(String sql, Object obj, Page page);

	/**
	 * 仅查询最多一条记录, 系统自动加上LIMIT 1
	 * 使用方法参考{@link BaseDao#queryBySql(String, Object)}
	 * @param sql MyBatis形式的SQL语句
	 * @param obj 参数, 可以是对象, 也可以是Map
	 * @return
	 */
	public Map<String, Object> queryOneBySql(String sql, Object obj);
	/**
	 * 返回更为方便的结果集对象{@link RowSet}, rs.getString(m, n) 表示取第m+1行n+1列的值并转成字符串
	 * @param sql MyBatis形式的SQL语句
	 * @param obj 参数, 可以是对象, 也可以是Map
	 * @return RowSet形式的结果集
	 */
	public RowSet queryBySqlToRowSet(String sql, Object obj);
	
	public RowSet queryBySqlToRowSet(String sql, Object obj, Page page);
	
	public RowSet queryOneBySqlToRowSet(String sql, Object obj);

	/**
	 * 查询后将结果转换为对应的类对象, 在字段与属性映射时忽略大小写<br>
	 * List&lt;Person&gt; list = queryBySql("SELECT * FROM member WHERE username = #{usermame}", member, Person.class);<br>
	 * List&lt;Person&gt; list = queryBySql("SELECT * FROM member WHERE username = #{usermame}", map, Person.class);<br>
	 * List&lt;Person&gt; list = queryBySql(sqlBuilder.getSql(), sqlBuilder.getParamMap(), Person.class);<br>
	 * @param <S> 返回结果对应的类名
	 * @param sql MyBatis形式的SQL语句
	 * @param obj 参数, 可以是对象, 也可以是Map
	 * @param clz 需要转换的类名
	 * @return
	 */
	public <S> List<S> queryBySqlToBean(String sql, Object obj, Class<S> clz);

	public <S> List<S> queryBySqlToBean(String sql, Object obj, Class<S> clz, Page page);

	/**
	 * 仅查询返回一条记录并转换为对应的类对象, 系统会自动在SQL后加上LIMIT 1<br>
	 * Person person = queryBySql("SELECT * FROM member WHERE username = #{usermame}", member, Person.class);<br>
	 * Person person = queryBySql("SELECT * FROM member WHERE username = #{usermame}", map, Person.class);<br>
	 * Person person = queryBySql(sqlBuilder.getSql(), sqlBuilder.getParamMap(), Person.class);<br>
	 * @param <S> 
	 * @param sql MyBatis形式的SQL语句
	 * @param obj 参数, 可以是对象, 也可以是Map
	 * @param clz
	 * @return
	 */
	public <S> S queryOneBySqlToBean(String sql, Object obj, Class<S> clz);

	public String queryBySqlToString(String sql, Object obj);

	public Integer queryBySqlToInteger(String sql, Object obj);

	public Double queryBySqlToDouble(String sql, Object obj);

	public int updateBySql(String sql, Object obj);

	public int deleteBySql(String sql, Object obj);

}
