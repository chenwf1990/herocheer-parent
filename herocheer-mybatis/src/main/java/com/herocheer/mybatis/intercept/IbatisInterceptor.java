package com.herocheer.mybatis.intercept;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.herocheer.cache.bean.RedisClient;
import com.herocheer.common.base.Page.Page;
import com.herocheer.common.base.entity.BaseEntity;
import com.herocheer.common.utils.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @desc ibatis拦截注入公共参数
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
@Component
@Intercepts({
            @Signature(type = Executor.class,method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
//        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
            @Signature(type = Executor.class,method = "update",args = {MappedStatement.class, Object.class}),
})
public class IbatisInterceptor implements Interceptor {
    private Properties properties;

    @Resource
    private RedisClient redisClient;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if(invocation.getArgs().length > 2){
            query(invocation);
        }else{
            update(invocation);
        }

        return invocation.proceed();
    }

    private Object query(Invocation invocation) throws SQLException {
        Object[] args = invocation.getArgs();
        Executor executor = (Executor) invocation.getTarget();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (sqlCommandType.SELECT.equals(sqlCommandType)) {
            //判断参数是否有Page参数，如果有则按照
            Page page = Page.getLocalPage();
            if (page != null) {
                Page.clearPage();
                final String sql = boundSql.getSql();
                //计算总的条目数
                final String countSQL = getMysqlCountSql(new StringBuffer(sql));
                int totalCount = getCounts(ms, countSQL, boundSql,invocation,parameterObject);
                //获取分页后的结果
                final String pageSql = this.getMysqlPageSql(page, new StringBuffer(sql));
                page.setTotalCount(totalCount);
                BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, pageSql);
                MappedStatement newMs = copyFromMappedStatement(ms,new BoundSqlSqlSource(newBoundSql));
                invocation.getArgs()[0]= newMs;
            }
        }
        return null;
    }

    /**
     * 利用新生成的SQL语句去替换原来的MappedStatement
     */
    private MappedStatement copyFromMappedStatement(MappedStatement ms, BoundSqlSqlSource boundSqlSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),ms.getId(),boundSqlSqlSource,ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if(ms.getKeyProperties() != null && ms.getKeyProperties().length !=0){
            StringBuffer keyProperties = new StringBuffer();
            for(String keyProperty : ms.getKeyProperties()){
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length()-1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
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
    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String pageSql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),pageSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }


    private void update(Invocation invocation) {
        final MappedStatement mappedStatement= (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if ((sqlCommandType.UPDATE.equals(sqlCommandType) || sqlCommandType.INSERT.equals(sqlCommandType)) && parameter != null) {
            if(parameter instanceof  BaseEntity){//单表处理
                BaseEntity entity = (BaseEntity) parameter;
                JSONObject json = getUserBaseInfo(entity);
                buildBaseEntity(json,entity,sqlCommandType);
            }else if (parameter instanceof Map){
                Map map = (Map) parameter;
                //批量处理
                if(map.containsKey("list") && map.get("list") != null && map.get("list") instanceof List){
                    ArrayList list = (ArrayList) map.get("list");
                    if (!list.isEmpty() && list.get(0) instanceof BaseEntity) {
                        List<BaseEntity> entitys = list;
                        JSONObject json = getUserBaseInfo(entitys.get(0));
                        for (BaseEntity baseEntity : entitys) {
                            buildBaseEntity(json, baseEntity, sqlCommandType);
                        }
                    }
                }else {//单表更新处理
                    if(sqlCommandType.UPDATE.equals(sqlCommandType)) {
                        JSONObject json = getUserBaseInfo(null);
                        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                        final String sql = boundSql.getSql();
                        String[] s = sql.split("where");
                        StringBuffer sb = new StringBuffer();
                        sb.append(s[0]);
                        sb.append(",updateId = " + json.getLong("id") + " ");
                        sb.append(",updateBy = '" + json.getString("userName") + "' ");
                        sb.append(",updateTime = " + System.currentTimeMillis() + " ");
                        sb.append(" where ");
                        sb.append(s[1] + " ");
                        BoundSql newBoundSql = copyFromBoundSql(mappedStatement, boundSql, sb.toString());
                        MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
                        invocation.getArgs()[0] = newMs;
                    }
                }
            }
        }
    }

    /**
     * 为对象的操作属性赋值
     *
     * @param bean
     */
    private void setProperty(Object bean, String name, Object value) {
        try {
            //根据需要，将相关属性赋上默认值
            BeanUtil.setProperty(bean, name, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildBaseEntity(JSONObject json, BaseEntity entity, SqlCommandType sqlCommandType) {
        if (sqlCommandType.UPDATE.equals(sqlCommandType)) {
            entity.setUpdateId(json.getLong("id"));
            entity.setUpdateTime(System.currentTimeMillis());
            entity.setUpdateBy(json.getString("userName"));
        } else if (sqlCommandType.INSERT.equals(sqlCommandType)) {
            entity.setCreatedId(json.getLong("id"));
            entity.setCreatedTime(System.currentTimeMillis());
            entity.setCreatedBy(json.getString("userName"));
        }
    }


    //备注：
    // 此处判断是因为分布式服务，服务在不同的虚拟机上，导致获取不到请求头的数据
    // 所有分布式的应用需要在对应的实体传tokenId
    private JSONObject getUserBaseInfo(BaseEntity entity) {
        String userBaseInfo = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            Object obj = request.getAttribute("userBaseInfo");
            if(obj != null){
                userBaseInfo = obj.toString();
            }
        }else{
            //此处不判断tokenId是否为空,直接让程序员必须传tokenId;
            userBaseInfo = redisClient.get(entity.getTokenId());
        }
        if(StringUtils.isEmpty(userBaseInfo)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",0);
            jsonObject.put("userName","system");
            return jsonObject;
        }
        return JSONObject.parseObject(userBaseInfo);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**

     组装分页SQL
     @param page 分页javaBean
     @param sqlBuffer 组装好的分页SQL
     @return
   */
    private String getMysqlPageSql(Page page, StringBuffer sqlBuffer) {
        sqlBuffer.append(" limit ").append((page.getPageNo() - 1) * page.getPageSize()).append(",").append(page.getPageSize());
        return sqlBuffer.toString();
    }

    /**

     组装查询数量的SQL
     @param sqlBuffer 组装好的分页SQL
     @return
     */
     private String getMysqlCountSql(StringBuffer sqlBuffer) {
         StringBuffer buffer = new StringBuffer(); buffer.append("select count(1) from ("); buffer.append(sqlBuffer);
         buffer.append(") as total");
         return buffer.toString();
     }



     /**获取总页数
      @return
       * @param mappedStatement
      * @param countSQL
      * @param invocation
      * @param parameter
      */
     private int getCounts(MappedStatement mappedStatement, String countSQL, BoundSql boundSql, Invocation invocation, Object parameter) {
         final ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameter, boundSql);
         Connection connection = null;
         ResultSet rs = null;
         PreparedStatement countStmt = null;
         int totpage = 0; try {
             //获取连接
             connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
             //预处理SQL
             countStmt = connection.prepareStatement(countSQL);
             //给预处理SQL 赋值
             parameterHandler.setParameters(countStmt);
             //查询预处理的SQL
             rs = countStmt.executeQuery();
             if (rs.next()) {
                 totpage = rs.getInt(1);
             }
         } catch (SQLException e) {
             e.printStackTrace();
         } finally {
             //关闭释放资源
             try {
                 if(rs != null) {
                     rs.close();
                 }
                 if(countStmt != null) {
                     countStmt.close();
                 }
                 if(connection != null) {
                     connection.close();
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
         return totpage;
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

    /**
     * 为对象的操作属性赋值
     *
     * @param obj
     */
    private void setProperty(final Object obj, final SqlCommandType sqlCommandType,JSONObject json) {
        try {
            if (sqlCommandType == SqlCommandType.INSERT) {
                BeanUtil.setProperty(obj, "createdId", json.getLong("id"));
                BeanUtil.setProperty(obj, "createdTime", System.currentTimeMillis());
                BeanUtil.setProperty(obj, "createdBy", json.getLong("userName"));
            } else {
                BeanUtil.setProperty(obj, "updateId", json.getLong("id"));
                BeanUtil.setProperty(obj, "updateTime", System.currentTimeMillis());
                BeanUtil.setProperty(obj, "updateBy", json.getLong("userName"));
            }
        } catch (Exception e) {

        }
    }
}
