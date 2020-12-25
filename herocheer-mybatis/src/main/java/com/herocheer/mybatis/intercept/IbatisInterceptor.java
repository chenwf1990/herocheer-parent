package com.herocheer.mybatis.intercept;

import com.herocheer.common.base.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @desc ibatis拦截注入公共参数
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
@Component
@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
public class IbatisInterceptor implements Interceptor {
    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType.UPDATE.equals(sqlCommandType)) {
            Object parameter = invocation.getArgs()[1];
            if (parameter != null && parameter instanceof BaseEntity) {
                BaseEntity entity = (BaseEntity) parameter;
                entity.setUpdateId(0L);
                entity.setUpdateTime(System.currentTimeMillis());
                entity.setUpdateBy("chenwf");
            }
        }else if (sqlCommandType.INSERT.equals(sqlCommandType)) {
            Object parameter = invocation.getArgs()[1];
            if (parameter != null && parameter instanceof BaseEntity) {
                BaseEntity entity = (BaseEntity) parameter;
                entity.setCreatedId(0L);
                entity.setCreatedTime(System.currentTimeMillis());
                entity.setCreatedBy("chenwf");
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
