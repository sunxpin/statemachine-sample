package com.baoxian.common.util;

import com.baoxian.common.dao.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.util.Map;

public class DynamicCreateClassUtil {
	private static Logger logger = LoggerFactory.getLogger(DynamicCreateClassUtil.class);

	@SuppressWarnings("unchecked")
	public static Class<? extends BaseDao<?>> createDaoClassByEntity(String packageName, String clzName) {
		String daoPackageName = packageName.substring(0, packageName.lastIndexOf(".")) + ".dao";
		String daoClzName = clzName + "Dao";
		boolean exist = ClassUtils.isPresent(daoPackageName + "." + daoClzName, Thread.currentThread().getContextClassLoader());
		if (exist)
			return null;
		try {
			StringBuffer sourceCode = new StringBuffer();
			sourceCode.append("package " + daoPackageName + ";\n\n");
			sourceCode.append("import com.baoxian.common.dao.BaseDao;\n");
			sourceCode.append("import " + packageName + "." + clzName + ";\n\n");
			sourceCode.append("public interface " + daoClzName + " extends BaseDao<" + clzName + "> { }");
			JavaStringCompiler compiler = new JavaStringCompiler();
			/**
			 * daoClzName : StateDao
			 * com/baoxian/common/dao/StateDao.java
			 *
			 * import com.baoxian.common.dao.BaseDao;
			 * import com.baoxian.entity.State;
			 *
			 * public interface StateDao extends BaseDao<State> { }
			 *
			 *
			 * package com.baoxian.common.statemachine.dao;
			 *
			 * import com.baoxian.common.dao.BaseDao;
			 * import com.baoxian.common.statemachine.entity.Log;
			 *
			 * public interface LogDao extends BaseDao<Log> { }
			 */
			Map<String, byte[]> results = compiler.compile(daoPackageName.replace(".", "/") + "/" + daoClzName + ".java", sourceCode.toString());
			return (Class<? extends BaseDao<?>>) compiler.loadClass(daoPackageName + "." + daoClzName, results);
		} catch (Exception e) {
			logger.error("Dynamic create dao class failed: " + daoPackageName + "." + daoClzName, e);
		}
		return null;
	}

	public static Class<?> createServiceClassByEntity(String packageName, String clzName) {
		String servicePackageName = packageName.substring(0, packageName.lastIndexOf(".")) + ".dbservice";
		String serviceClzName = clzName + "Service";
		boolean exist = ClassUtils.isPresent(servicePackageName + "." + serviceClzName, Thread.currentThread().getContextClassLoader());
		if (exist)
			return null;
		try {
			StringBuffer sourceCode = new StringBuffer();
			sourceCode.append("package " + servicePackageName + ";\n\n");
			sourceCode.append("import com.baoxian.common.services.BaseDaoService;\n");
			sourceCode.append("import org.springframework.stereotype.Service;\n");
			sourceCode.append("import " + packageName + "." + clzName + ";\n\n");
			sourceCode.append("@Service\n");
			sourceCode.append("public class " + serviceClzName + " extends BaseDaoService<" + clzName + "> { }");
			JavaStringCompiler compiler = new JavaStringCompiler();
			Map<String, byte[]> results = compiler.compile(
					servicePackageName.replace(".", "/") + "/" + serviceClzName + ".java", sourceCode.toString());
			return compiler.loadClass(servicePackageName + "." + serviceClzName, results);
		} catch (Exception e) {
			logger.error("Dynamic create service class failed: " + servicePackageName + "." + serviceClzName, e);
		}
		return null;
	}
}
