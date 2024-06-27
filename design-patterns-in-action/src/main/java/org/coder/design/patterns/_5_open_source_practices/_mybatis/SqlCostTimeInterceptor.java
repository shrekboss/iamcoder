package org.coder.design.patterns._5_open_source_practices._mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.Properties;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */

/**
 * 明确地标明拦截的目标方法。{@link Intercepts} 注解实际上就是起了这个作用。其中，{@link Intercepts} 注解又可以嵌套 {@link Signature} 注解。
 * 一个 {@link Signature} 注解标明一个要拦截的目标方法
 *
 * {@link Signature} 注解包含三个元素：type、method、args。
 * type 指明要拦截的类
 * method 指明方法名
 * args 指明方法的参数列表
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class SqlCostTimeInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(SqlCostTimeInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) target;
        try {
            return invocation.proceed();
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();
            logger.info("执行 SQL：[ {} ]执行耗时[ {} ms]", sql, costTime);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("插件配置的信息：" + properties);
    }
}

/**
 * <!-- MyBatis全局配置文件：mybatis-config.xml -->
 * <plugins>
 * <plugin interceptor="org.coder.design.patterns._5_open_source_practices._mybatis.SqlCostTimeInterceptor">
 * <property name="someProperty" value="100"/>
 * </plugin>
 * </plugins>
 */

