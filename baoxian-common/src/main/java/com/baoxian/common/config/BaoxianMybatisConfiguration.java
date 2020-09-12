package com.baoxian.common.config;

import com.baoxian.common.annotation.Table;
import com.baoxian.common.entity.BaseEntity;
import com.baoxian.common.interceptor.*;
import com.baoxian.common.util.BaoxianUtil;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author 李德英
 */
public class BaoxianMybatisConfiguration {

    public static void config(Configuration configuration) {
        addBaseInterceptor(configuration);
        buildBaseStatement(configuration);
        BaoxianUtil.getDaoNameMap().forEach((daoClzName, entityClass) -> {
            if (!BaseEntity.class.isAssignableFrom(entityClass)) return;
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                /**
                 *<p>
                 * xml解析逻辑位于{@link XMLMapperBuilder#configurationElement(org.apache.ibatis.parsing.XNode)}
                 * ResultMap注解解析逻辑位于{@link MapperAnnotationBuilder#parseResultMap(java.lang.reflect.Method)}
                 * 解析结果会注册到{@link Configuration#resultMaps}中
                 *</p>
                 */
                /*Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    if (!StringUtils.hasText(column.name())) {
                        throw new StructureException("实体" + field.getDeclaringClass().getSimpleName() + "的属性" + field.getName() + "的@Column注解没有name");
                    }
                    Class<?> type = field.getType();
                    String name = column.name();
                }
                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                if (manyToOne != null) {
                    JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
                    if (joinColumn != null) {
                        if (!StringUtils.hasText(joinColumn.name())) {
                            throw new StructureException("实体" + field.getDeclaringClass().getSimpleName() + "的属性" + field.getName() + "没有@JoinColumn注解，或者注解没有设置name");
                        }
                        String name = column.name();
                        String joinColumnName = joinColumn.name();
                        Class<?> joinType = field.getType();
                        Field[] joinDeclaredFields = joinType.getDeclaredFields();
                        for (Field joinDeclaredField : joinDeclaredFields) {
                            String joinDeclaredFieldName = joinDeclaredField.getName();
                            Class<?> joinDeclaredFieldType = joinDeclaredField.getType();
                        }
                    }
                }
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                if (manyToMany != null) {
                    ResolvableType resolvableType = ResolvableType.forField(field);
                    Class<?> columnType = resolvableType.getGeneric(0).resolve();
                    if (manyToMany.mappedBy() != null) {
                        Field otherField;
                        try {
                            otherField = columnType.getDeclaredField(manyToMany.mappedBy());
                            String joinTable = otherField.getDeclaredAnnotation(JoinTable.class).name();
                            String joinColumn = otherField.getDeclaredAnnotation(JoinTable.class).inverseJoinColumns()[0].name();
                            String inverseJoinColumn = otherField.getDeclaredAnnotation(JoinTable.class).joinColumns()[0].name();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                }
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                if (oneToMany != null) {
                    Class<?> type = field.getType();
                    String name = column.name();
                }

                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                if (oneToOne != null) {
                    Class<?> type = field.getType();
                    String name = column.name();
                }*/
            }

            Table table = entityClass.getAnnotation(Table.class);
            String tableName = null;
            if (table != null)
                tableName = table.value();
            else {
                // 反向推断表名: 第二个大写字母前加下划线
                int index = 1;
                tableName = entityClass.getSimpleName();
                char[] chars = entityClass.getSimpleName().substring(1).toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if ((char) chars[i] >= 'A' && (char) chars[i] <= 'Z') {
                        index = i;
                        break;
                    }
                }
                tableName = tableName.substring(0, index + 1).toUpperCase() + "_" + tableName.substring(index + 1);
            }

            String xml = buildresultMap(configuration, daoClzName, entityClass);
            xml = buildGeneralStatement(configuration, daoClzName, entityClass, tableName);
            if (xml == null)  return;
            XPathParser parser = new XPathParser(xml);
//            List<XNode> resultMap = parser.evalNode("/mapper").evalNodes("/mapper/resultMap");
//            resultMapElements(configuration, resultMap, daoClzName);
            List<XNode> list = parser.evalNode("/mapper").evalNodes("select|insert|update|delete");
            buildStatementFromContext(configuration, list, daoClzName, null);

        });
    }

    private static String buildresultMap(Configuration configuration, String daoClzName, Class<?> entityClass) {
        return null;
    }

    public static void addBaseInterceptor(Configuration configuration) {
        configuration.addInterceptor(new SlaveInterceptor());
        configuration.addInterceptor(new PageInterceptor());
        configuration.addInterceptor(new ExecutorQueryInterceptor());
        configuration.addInterceptor(new ExecutorUpdateInterceptor());
        configuration.addInterceptor(new LoggerInterceptor());
    }

    private static void buildBaseStatement(Configuration configuration) {
        String xml = "<mapper namespace=\"com.baoxian.common.dao.BaseDao\">"
                + "<select id=\"queryBySql\" resultType=\"java.util.Map\">${sql}</select>"
                + "<select id=\"queryOneBySql\" resultType=\"java.util.Map\">${sql} LIMIT 1</select>"
                + "<select id=\"queryBySqlToRowSet\" resultType=\"java.util.LinkedHashMap\">${sql}</select>"
                + "<select id=\"queryOneBySqlToRowSet\" resultType=\"java.util.LinkedHashMap\">${sql} LIMIT 1</select>"
                + "<select id=\"queryBySqlToBean\" resultType=\"java.util.Map\">${sql}</select>"
                + "<select id=\"queryOneBySqlToBean\" resultType=\"java.util.Map\">${sql} LIMIT 1</select>"
                + "<select id=\"queryBySqlToString\" resultType=\"String\">${sql} LIMIT 1</select>"
                + "<select id=\"queryBySqlToInteger\" resultType=\"Integer\">${sql} LIMIT 1</select>"
                + "<select id=\"queryBySqlToDouble\" resultType=\"Double\">${sql} LIMIT 1</select>"
                + "<update id=\"updateBySql\">${sql}</update>" + "<delete id=\"deleteBySql\">${sql}</delete></mapper>";
        XPathParser parser = new XPathParser(xml);
        List<XNode> list = parser.evalNode("/mapper").evalNodes("select|insert|update|delete");
        buildStatementFromContext(configuration, list, "com.baoxian.common.dao.BaseDao", null);
    }

    private static void resultMapElements(Configuration configuration, List<XNode> list, String daoClassName) {
        for (XNode resultMapNode : list) {
            try {
                resultMapElement(configuration, resultMapNode, daoClassName);
            } catch (IncompleteElementException e) {
                // ignore, it will be retried
            }
        }
    }

    private static ResultMap resultMapElement(Configuration configuration, XNode resultMapNode, String daoClassName) {
        return resultMapElement(configuration, resultMapNode, Collections.emptyList(), null, daoClassName);
    }

    private static ResultMap resultMapElement(Configuration configuration, XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType, String daoClassName) {
        ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
        String type = resultMapNode.getStringAttribute("type",
                resultMapNode.getStringAttribute("ofType",
                        resultMapNode.getStringAttribute("resultType",
                                resultMapNode.getStringAttribute("javaType"))));
        Class<?> typeClass = resolveClass(configuration, type);
        if (typeClass == null) {
            typeClass = inheritEnclosingType(configuration, resultMapNode, enclosingType);
        }
        Discriminator discriminator = null;
        List<ResultMapping> resultMappings = new ArrayList<>(additionalResultMappings);
        List<XNode> resultChildren = resultMapNode.getChildren();
        for (XNode resultChild : resultChildren) {
            if ("constructor".equals(resultChild.getName())) {
                processConstructorElement(configuration, resultChild, typeClass, resultMappings, daoClassName);
            } else if ("discriminator".equals(resultChild.getName())) {
                discriminator = processDiscriminatorElement(configuration, resultChild, typeClass, resultMappings, daoClassName);
            } else {
                List<ResultFlag> flags = new ArrayList<>();
                if ("id".equals(resultChild.getName())) {
                    flags.add(ResultFlag.ID);
                }
                resultMappings.add(buildResultMappingFromContext(configuration, resultChild, typeClass, flags, daoClassName));
            }
        }
        String id = resultMapNode.getStringAttribute("id",
                resultMapNode.getValueBasedIdentifier());
        String extend = resultMapNode.getStringAttribute("extends");
        Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");

        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, daoClassName);
        assistant.setCurrentNamespace(daoClassName);
        ResultMapResolver resultMapResolver = new ResultMapResolver(assistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
        try {
            return resultMapResolver.resolve();
        } catch (IncompleteElementException e) {
            configuration.addIncompleteResultMap(resultMapResolver);
            throw e;
        }
    }

    protected static <T> Class<? extends T> resolveClass(Configuration configuration, String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return resolveAlias(configuration, alias);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class. Cause: " + e, e);
        }
    }

    protected static <T> Class<? extends T> resolveAlias(Configuration configuration, String alias) {
        return configuration.getTypeAliasRegistry().resolveAlias(alias);
    }

    private static void processConstructorElement(Configuration configuration, XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings, String daoClassName) {
        List<XNode> argChildren = resultChild.getChildren();
        for (XNode argChild : argChildren) {
            List<ResultFlag> flags = new ArrayList<>();
            flags.add(ResultFlag.CONSTRUCTOR);
            if ("idArg".equals(argChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            resultMappings.add(buildResultMappingFromContext(configuration, argChild, resultType, flags, daoClassName));
        }
    }

    private static Discriminator processDiscriminatorElement(Configuration configuration, XNode context, Class<?> resultType, List<ResultMapping> resultMappings, String daoClassName) {
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String typeHandler = context.getStringAttribute("typeHandler");
        Class<?> javaTypeClass = resolveClass(configuration, javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(configuration, typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        Map<String, String> discriminatorMap = new HashMap<>();
        for (XNode caseChild : context.getChildren()) {
            String value = caseChild.getStringAttribute("value");
            String resultMap = caseChild.getStringAttribute("resultMap", processNestedResultMappings(configuration, caseChild, resultMappings, resultType, daoClassName));
            discriminatorMap.put(value, resultMap);
        }

        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, daoClassName);
        assistant.setCurrentNamespace(daoClassName);
        return assistant.buildDiscriminator(resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass, discriminatorMap);
    }

    private static void buildStatementFromContext(Configuration configuration, List<XNode> list, String daoClassName,
                                                  String requiredDatabaseId) {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, daoClassName);
        assistant.setCurrentNamespace(daoClassName);
        for (XNode context : list) {
            if (configuration.hasStatement(daoClassName + "." + context.getStringAttribute("id")))
                continue;
            final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, assistant, context, requiredDatabaseId);
            try {
                statementParser.parseStatementNode();
            } catch (IncompleteElementException e) {
                configuration.addIncompleteStatement(statementParser);
            }
        }
    }

    private static ResultMapping buildResultMappingFromContext(Configuration configuration, XNode context, Class<?> resultType, List<ResultFlag> flags, String daoClassName) {

        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, daoClassName);
        assistant.setCurrentNamespace(daoClassName);
        String property;
        if (flags.contains(ResultFlag.CONSTRUCTOR)) {
            property = context.getStringAttribute("name");
        } else {
            property = context.getStringAttribute("property");
        }
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String nestedSelect = context.getStringAttribute("select");
        String nestedResultMap = context.getStringAttribute("resultMap", processNestedResultMappings(configuration, context, Collections.emptyList(), resultType, daoClassName));
        String notNullColumn = context.getStringAttribute("notNullColumn");
        String columnPrefix = context.getStringAttribute("columnPrefix");
        String typeHandler = context.getStringAttribute("typeHandler");
        String resultSet = context.getStringAttribute("resultSet");
        String foreignColumn = context.getStringAttribute("foreignColumn");
        boolean lazy = "lazy".equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
        Class<?> javaTypeClass = resolveClass(configuration, javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(configuration, typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        return assistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet, foreignColumn, lazy);
    }

    protected static JdbcType resolveJdbcType(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return JdbcType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
        }
    }

    private static String processNestedResultMappings(Configuration configuration, XNode context, List<ResultMapping> resultMappings, Class<?> enclosingType, String daoClassName) {
        if (Arrays.asList("association", "collection", "case").contains(context.getName())
                && context.getStringAttribute("select") == null) {
            validateCollection(configuration, context, enclosingType);
            ResultMap resultMap = resultMapElement(configuration, context, resultMappings, enclosingType, daoClassName);
            return resultMap.getId();
        }
        return null;
    }

    protected static void validateCollection(Configuration configuration, XNode context, Class<?> enclosingType) {
        if ("collection".equals(context.getName()) && context.getStringAttribute("resultMap") == null
                && context.getStringAttribute("javaType") == null) {
            MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
            String property = context.getStringAttribute("property");
            if (!metaResultType.hasSetter(property)) {
                throw new BuilderException(
                        "Ambiguous collection type for property '" + property + "'. You must specify 'javaType' or 'resultMap'.");
            }
        }
    }

    protected static Class<?> inheritEnclosingType(Configuration configuration, XNode resultMapNode, Class<?> enclosingType) {
        if ("association".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
            String property = resultMapNode.getStringAttribute("property");
            if (property != null && enclosingType != null) {
                MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
                return metaResultType.getSetterType(property);
            }
        } else if ("case".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
            return enclosingType;
        }
        return null;
    }

    public static String buildGeneralStatement(Configuration configuration, String daoClassName, Class<?> entityClass,
                                               String tableName) {
        List<Field> fields = new ArrayList<>();
        Class<?> tempClass = entityClass;
        while (tempClass != null) {
            fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        if (fields.size() == 0)
            return null;
        StringBuilder buffer = new StringBuilder();
        buffer.append(buildInsert(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildInsertOrUpdate(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildInsertOrUpdateAll(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildInsertByBatch(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildQueryById(configuration, daoClassName, entityClass, tableName));
        buffer.append(buildQueryByIds(configuration, daoClassName, entityClass, tableName));
        buffer.append(buildQuery(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildQueryByPage(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildQueryByAppender(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildQueryByWhere(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildCount(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildCountByWhere(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildQueryOne(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdate(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateById(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateByIds(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateByWhere(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateAll(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateAllById(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateAllByIds(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateAllByWhere(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildDeleteById(configuration, daoClassName, entityClass, tableName));
        buffer.append(buildDeleteByIds(configuration, daoClassName, entityClass, tableName));
        buffer.append(buildDeleteByWhere(configuration, daoClassName, entityClass, tableName));
        buffer.append(buildDelete(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildQueryBySqlBuilder(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildQueryOneBySqlBuilder(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildCountBySqlBuilder(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildDeleteBySqlBuilder(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateBySqlBuilder(configuration, daoClassName, entityClass, fields, tableName));
        buffer.append(buildUpdateAllBySqlBuilder(configuration, daoClassName, entityClass, fields, tableName));
        return "<mapper>" + buffer.toString() + "</mapper>";
//        return buffer.toString() + "</mapper>";
    }

    private static String buildQueryBySqlBuilder(Configuration configuration, String daoClassName, Class<?> entityClass,
                                                 List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryBySqlBuilder"))
            return "";
        return "<select id=\"queryBySqlBuilder\" parameterType=\"String\" resultType=\"" + entityClass.getName()
                + "\">SELECT * FROM " + tableName + "<where>${sql}</where></select>";
    }

    private static String buildQueryOneBySqlBuilder(Configuration configuration, String daoClassName, Class<?> entityClass,
                                                    List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryBySqlBuilder"))
            return "";
        return "<select id=\"queryOneBySqlBuilder\" parameterType=\"String\" resultType=\"" + entityClass.getName()
                + "\">SELECT * FROM " + tableName + "<where>${sql}</where> LIMIT 1</select>";
    }

    private static String buildDeleteBySqlBuilder(Configuration configuration, String daoClassName,
                                                  Class<?> entityClass, List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".deleteBySqlBuilder"))
            return "";
        return "<delete id=\"deleteBySqlBuilder\" resultType=\"java.lang.Integer\">DELETE FROM " + tableName
                + "${sql}</delete>";
    }

    private static String buildUpdateBySqlBuilder(Configuration configuration, String daoClassName,
                                                  Class<?> entityClass, List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateBySqlBuilder"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append("<if test=\"obj." + field.getName() + " != null\">" + field.getName() + " = #{obj."
                    + field.getName() + "}, </if>");
        }
        return "<update id=\"updateBySqlBuilder\" resultType=\"java.lang.Integer\">UPDATE " + tableName + "<set>" + sets
                + "modifyDate = NOW()</set>${sqlBuilder.sql}</update>";
    }

    private static String buildUpdateAllBySqlBuilder(Configuration configuration, String daoClassName,
                                                     Class<?> entityClass, List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateAllBySqlBuilder"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append(field.getName() + " = #{obj." + field.getName() + "}, ");
        }
        return "<update id=\"updateAllBySqlBuilder\" resultType=\"java.lang.Integer\">UPDATE " + tableName + "<set>"
                + sets + "modifyDate = NOW()</set>${sqlBuilder.sql}</update>";
    }

    private static String buildCountByWhere(Configuration configuration, String daoClassName, Class<?> entityClass,
                                            List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".countByWhere"))
            return "";
        return "<select id=\"countByWhere\" parameterType=\"String\" resultType=\"java.lang.Integer\">"
                + "SELECT COUNT(*) FROM " + tableName + "<where>${where}</where></select>";
    }

    private static String buildCountBySqlBuilder(Configuration configuration, String daoClassName, Class<?> entityClass,
                                                 List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".countBySqlBuilder"))
            return "";
        return "<select id=\"countBySqlBuilder\" parameterType=\"String\" resultType=\"java.lang.Integer\">"
                + "SELECT COUNT(*) FROM " + tableName + "<where>${sql}</where></select>";
    }

    private static String buildQueryById(Configuration configuration, String daoClassName, Class<?> entityClass,
                                         String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryById"))
            return "";
        return "<select id=\"queryById\" parameterType=\"String\" resultType=\"" + entityClass.getName()
                + "\">SELECT * FROM " + tableName + " WHERE id = #{id}</select>";
    }

    private static String buildQueryByIds(Configuration configuration, String daoClassName, Class<?> entityClass,
                                          String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryByIds"))
            return "";
        return "<select id=\"queryByIds\" parameterType=\"java.util.List\" resultType=\"" + entityClass.getName()
                + "\">SELECT * FROM " + tableName
                + " WHERE id IN <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\""
                + " open=\"(\" close=\")\">#{item}</foreach></select>";
    }

    private static String buildInsert(Configuration configuration, String daoClassName, Class<?> entityClass,
                                      List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".insert"))
            return "";
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        fields.forEach(e -> {
            cols.append(", " + e.getName());
            if (e.getName().matches("(?i)(createDate|modifyDate)"))
                vals.append(", NOW()");
            else
                vals.append(", #{" + e.getName() + "}");
        });

        return "<insert id=\"insert\" parameterType=\"" + entityClass.getName() + "\">INSERT INTO " + tableName + "("
                + cols.toString().substring(2) + ") VALUES(" + vals.toString().substring(2) + ")</insert>";
    }

    private static String buildInsertOrUpdate(Configuration configuration, String daoClassName, Class<?> entityClass,
                                              List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".insertOrUpdate"))
            return "";
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        fields.forEach(e -> {
            cols.append(", " + e.getName());
            if (e.getName().matches("(?i)(createDate|modifyDate)"))
                vals.append(", NOW()");
            else
                vals.append(", #{entity." + e.getName() + "}");
        });
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append("<if test=\"entity." + field.getName() + " != null\">" + field.getName() + " = #{entity."
                    + field.getName() + "}, </if>");
        }

        return "<insert id=\"insertOrUpdate\" parameterType=\"" + entityClass.getName() + "\">"
                + "<selectKey keyProperty=\"modifyDate\" resultType=\"java.util.Date\" order=\"BEFORE\">"
                + "SELECT CASE COUNT(*) WHEN 0 THEN NOW() ELSE NULL END AS modifyDate FROM " + tableName + " WHERE"
                + "<choose><if test=\"fields == null\">id = #{entity.id}</if>"
                + "<otherwise><foreach collection=\"fields\" item=\"field\" separator=\"AND\">${field} = #{entity.${field}}</foreach></otherwise></choose></selectKey>"
                + "<if test=\"modifyDate == null\">UPDATE " + tableName + "<set>" + sets.toString()
                + "modifyDate = NOW()</set>WHERE" + "<choose><if test=\"fields == null\">id = #{entity.id}</if>"
                + "<otherwise><foreach collection=\"fields\" item=\"field\" separator=\"AND\">${field} = #{entity.${field}}</foreach></otherwise></choose> "
                + "</if><if test=\"modifyDate != null\">" + "INSERT INTO " + tableName + "("
                + cols.toString().substring(2) + ") VALUES(" + vals.toString().substring(2) + ")</if></insert>";
    }

    private static String buildInsertOrUpdateAll(Configuration configuration, String daoClassName, Class<?> entityClass,
                                                 List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".insertOrUpdateAll"))
            return "";
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        fields.forEach(e -> {
            cols.append(", " + e.getName());
            if (e.getName().matches("(?i)(createDate|modifyDate)"))
                vals.append(", NOW()");
            else
                vals.append(", #{entity." + e.getName() + "}");
        });
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append(field.getName() + " = #{entity." + field.getName() + "}, ");
        }

        return "<insert id=\"insertOrUpdateAll\" parameterType=\"" + entityClass.getName() + "\">"
                + "<selectKey keyProperty=\"modifyDate\" resultType=\"java.util.Date\" order=\"BEFORE\">"
                + "SELECT CASE COUNT(*) WHEN 0 THEN NOW() ELSE NULL END AS modifyDate FROM " + tableName + " WHERE "
                + "<choose><if test=\"fields == null\">id = #{entity.id}</if>"
                + "<otherwise><foreach collection=\"fields\" item=\"field\" separator=\"AND\">${field} = #{entity.${field}}</foreach></otherwise></choose></selectKey>"
                + "<if test=\"modifyDate == null\">UPDATE " + tableName + "<set>" + sets.toString()
                + "modifyDate = NOW()</set>WHERE" + "<choose><if test=\"fields == null\">id = #{entity.id}</if>"
                + "<otherwise><foreach collection=\"fields\" item=\"field\" separator=\"AND\">${field} = #{entity.${field}}</foreach></otherwise></choose> "
                + "</if><if test=\"modifyDate != null\">" + "INSERT INTO " + tableName + "("
                + cols.toString().substring(2) + ") VALUES(" + vals.toString().substring(2) + ")</if></insert>";
    }

    private static String buildInsertByBatch(Configuration configuration, String daoClassName, Class<?> entityClass,
                                             List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".insertByBatch"))
            return "";
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        fields.forEach(e -> {
            cols.append(", " + e.getName());
            if (e.getName().matches("(?i)(createDate|modifyDate)"))
                vals.append(", NOW()");
            else
                vals.append(", #{item." + e.getName() + "}");
        });
        return "<insert id=\"insertByBatch\" parameterType=\"java.util.List\">INSERT INTO " + tableName + "("
                + cols.toString().substring(2)
                + ") VALUES<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">("
                + vals.toString().substring(2) + ")</foreach></insert>";
    }

    private static String buildQuery(Configuration configuration, String daoClassName, Class<?> entityClass,
                                     List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".query"))
            return "";
        StringBuilder wheres = new StringBuilder();
        for (Field field : fields) {
            if ("id".equalsIgnoreCase(field.getName()))
                continue;
            wheres.append("<if test=\"" + field.getName() + " != null\">AND " + field.getName() + " = #{"
                    + field.getName() + "} </if>");
        }
        return "<select id=\"query\" parameterType=\"" + entityClass.getName() + "\" resultType=\""
                + entityClass.getName() + "\">SELECT * FROM " + tableName + "<where>" + wheres.toString()
                + "</where></select>";
    }

    private static String buildQueryByPage(Configuration configuration, String daoClassName, Class<?> entityClass,
                                           List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryByPage"))
            return "";
        StringBuilder wheres = new StringBuilder();
        wheres.append("<if test=\"obj != null\"><where>");
        for (Field field : fields) {
            if ("id".equalsIgnoreCase(field.getName()))
                continue;
            wheres.append("<if test=\"obj." + field.getName() + " != null\">AND " + field.getName() + " = #{obj."
                    + field.getName() + "} </if>");
        }
        wheres.append("</where></if>");
        return "<select id=\"queryByPage\" parameterType=\"" + entityClass.getName() + "\" resultType=\""
                + entityClass.getName() + "\">SELECT * FROM " + tableName + wheres.toString() + "</select>";
    }

    private static String buildQueryByAppender(Configuration configuration, String daoClassName, Class<?> entityClass,
                                               List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryByAppender"))
            return "";
        StringBuilder wheres = new StringBuilder();
        wheres.append("<if test=\"entity != null\"><where>");
        for (Field field : fields) {
            if ("id".equalsIgnoreCase(field.getName()))
                continue;
            wheres.append("<if test=\"entity." + field.getName() + " != null\">AND " + field.getName() + " = #{entity."
                    + field.getName() + "} </if>");
        }
        wheres.append("</where></if>");
        wheres.append("<if test=\"appender != null and appender != ''\">${appender}</if>");
        return "<select id=\"queryByAppender\" parameterType=\"" + entityClass.getName() + "\" resultType=\""
                + entityClass.getName() + "\">SELECT * FROM " + tableName + wheres.toString() + "</select>";
    }

    private static String buildQueryByWhere(Configuration configuration, String daoClassName, Class<?> entityClass,
                                            List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryByWhere"))
            return "";
        return "<select id=\"queryByWhere\" parameterType=\"" + entityClass.getName() + "\" resultType=\""
                + entityClass.getName() + "\">SELECT * FROM " + tableName + "<where>${where}</where></select>";
    }

    private static String buildCount(Configuration configuration, String daoClassName, Class<?> entityClass,
                                     List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".count"))
            return "";
        StringBuilder wheres = new StringBuilder();
        for (Field field : fields) {
            if ("id".equalsIgnoreCase(field.getName()))
                continue;
            wheres.append("<if test=\"" + field.getName() + " != null\">AND " + field.getName() + " = #{"
                    + field.getName() + "} </if>");
        }
        return "<select id=\"count\" parameterType=\"" + entityClass.getName() + "\" resultType=\""
                + "java.lang.Integer\">SELECT COUNT(*) FROM " + tableName + "<where>" + wheres.toString()
                + "</where></select>";
    }

    private static String buildQueryOne(Configuration configuration, String daoClassName, Class<?> entityClass,
                                        List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".queryOne"))
            return "";
        StringBuilder wheres = new StringBuilder();
        for (Field field : fields) {
            if ("id".equalsIgnoreCase(field.getName()))
                continue;
            wheres.append("<if test=\"" + field.getName() + " != null\">AND " + field.getName() + " = #{"
                    + field.getName() + "} </if>");
        }
        return "<select id=\"queryOne\" parameterType=\"" + entityClass.getName() + "\" resultType=\""
                + entityClass.getName() + "\">SELECT * FROM " + tableName + "<where>" + wheres.toString()
                + "</where>LIMIT 1 </select>";
    }

    private static String buildUpdateById(Configuration configuration, String daoClassName, Class<?> entityClass,
                                          List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateById"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append("<if test=\"" + field.getName() + " != null\">" + field.getName() + " = #{" + field.getName()
                    + "}, </if>");
        }
        return "<update id=\"updateById\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName + "<set>"
                + sets.toString() + "modifyDate = NOW()</set>WHERE id = #{id}</update>";
    }

    private static String buildUpdateAllById(Configuration configuration, String daoClassName, Class<?> entityClass,
                                             List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateAllById"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append(field.getName() + " = #{" + field.getName() + "}, ");
        }
        return "<update id=\"updateAllById\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName
                + "<set>" + sets.toString() + "modifyDate = NOW()</set>WHERE id = #{id}</update>";
    }

    private static String buildUpdate(Configuration configuration, String daoClassName, Class<?> entityClass,
                                      List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".update"))
            return "";
        StringBuilder sets = new StringBuilder();
        StringBuilder wheres = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)id"))
                continue;
            wheres.append("<if test=\"obj." + field.getName() + " != null\">AND " + field.getName() + " = #{obj."
                    + field.getName() + "} </if>");
            if (field.getName().matches("(?i)(createDate|modifyDate)"))
                continue;
            sets.append("<if test=\"entity." + field.getName() + " != null\">" + field.getName() + " = #{entity."
                    + field.getName() + "}, </if>");
        }
        return "<update id=\"update\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName + "<set>"
                + sets.toString() + "modifyDate = NOW()</set><where>" + wheres.toString() + "</where></update>";
    }

    private static String buildUpdateAll(Configuration configuration, String daoClassName, Class<?> entityClass,
                                         List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateAll"))
            return "";
        StringBuilder sets = new StringBuilder();
        StringBuilder wheres = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)id"))
                continue;
            wheres.append("<if test=\"obj." + field.getName() + " != null\">AND " + field.getName() + " = #{obj."
                    + field.getName() + "} </if>");
            if (field.getName().matches("(?i)(createDate|modifyDate)"))
                continue;
            sets.append(field.getName() + " = #{entity." + field.getName() + "}, ");
        }
        return "<update id=\"updateAll\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName + "<set>"
                + sets.toString() + "modifyDate = NOW()</set><where>" + wheres.toString() + "</where></update>";
    }

    private static String buildUpdateByIds(Configuration configuration, String daoClassName, Class<?> entityClass,
                                           List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateByIds"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append("<if test=\"obj." + field.getName() + " != null\">" + field.getName() + " = #{obj."
                    + field.getName() + "}, </if>");
        }
        return "<update id=\"updateByIds\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName
                + "<set>" + sets.toString() + "modifyDate = NOW()</set>"
                + "WHERE id IN <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\""
                + " open=\"(\" close=\")\">#{item}</foreach></update>";
    }

    private static String buildUpdateByWhere(Configuration configuration, String daoClassName, Class<?> entityClass,
                                             List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateByWhere"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append("<if test=\"entity." + field.getName() + " != null\">" + field.getName() + " = #{entity."
                    + field.getName() + "}, </if>");
        }
        return "<update id=\"updateByWhere\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName
                + "<set>" + sets.toString() + "modifyDate = NOW()</set>" + "<where>${where}</where></update>";
    }

    private static String buildUpdateAllByIds(Configuration configuration, String daoClassName, Class<?> entityClass,
                                              List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateAllByIds"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append(field.getName() + " = #{obj." + field.getName() + "}, ");
        }
        return "<update id=\"updateAllByIds\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName
                + "<set>" + sets.toString() + "modifyDate = NOW()</set>"
                + "WHERE id IN <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\""
                + " open=\"(\" close=\")\">#{item}</foreach></update>";
    }

    private static String buildUpdateAllByWhere(Configuration configuration, String daoClassName, Class<?> entityClass,
                                                List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".updateAllByWhere"))
            return "";
        StringBuilder sets = new StringBuilder();
        for (Field field : fields) {
            if (field.getName().matches("(?i)(id|createDate|modifyDate)"))
                continue;
            sets.append(field.getName() + " = #{entity." + field.getName() + "}, ");
        }
        return "<update id=\"updateAllByWhere\" parameterType=\"" + entityClass.getName() + "\">UPDATE " + tableName
                + "<set>" + sets.toString() + "modifyDate = NOW()</set>" + "<where>${where}</where></update>";
    }

    private static String buildDeleteById(Configuration configuration, String daoClassName, Class<?> entityClass,
                                          String tableName) {
        if (configuration.hasStatement(daoClassName + ".deleteById"))
            return "";
        return "<delete id=\"deleteById\" parameterType=\"String\">DELETE FROM " + tableName
                + " WHERE id = #{id}</delete>";
    }

    private static String buildDeleteByIds(Configuration configuration, String daoClassName, Class<?> entityClass,
                                           String tableName) {
        if (configuration.hasStatement(daoClassName + ".deleteByIds"))
            return "";
        return "<delete id=\"deleteByIds\" parameterType=\"String\">DELETE FROM " + tableName + " WHERE id IN "
                + "<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\" open=\"(\" close=\")\">"
                + "#{item}</foreach></delete>";
    }

    private static String buildDeleteByWhere(Configuration configuration, String daoClassName, Class<?> entityClass,
                                             String tableName) {
        if (configuration.hasStatement(daoClassName + ".deleteByWhere"))
            return "";
        return "<delete id=\"deleteByWhere\" parameterType=\"String\">DELETE FROM " + tableName
                + "<where>${where}</where></delete>";
    }

    private static String buildDelete(Configuration configuration, String daoClassName, Class<?> entityClass,
                                      List<Field> fields, String tableName) {
        if (configuration.hasStatement(daoClassName + ".delete"))
            return "";
        StringBuilder wheres = new StringBuilder();
        for (Field field : fields) {
            if ("id".equalsIgnoreCase(field.getName()))
                continue;
            wheres.append("<if test=\"" + field.getName() + " != null\">AND " + field.getName() + " = #{"
                    + field.getName() + "} </if>");
        }
        return "<delete id=\"delete\" parameterType=\"" + entityClass.getName() + "\">DELETE FROM " + tableName
                + "<where>" + wheres.toString() + "</where></delete>";
    }

}
