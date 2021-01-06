package com.herocheer.mybatis.intercept;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.herocheer.cache.bean.RedisClient;
import com.herocheer.common.base.Page.Page;
import com.herocheer.common.base.entity.BaseEntity;
import com.herocheer.common.constants.ResponseCode;
import com.herocheer.common.exception.CommonException;
import com.herocheer.common.utils.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private void query(Invocation invocation) {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (sqlCommandType.SELECT.equals(sqlCommandType)) {
            //判断参数是否有Page参数，如果有则按照
            Page page = Page.getLocalPage();
            if (page != null) {
                final String sql = boundSql.getSql();
                //计算总的条目数
                final String countSQL = getMysqlCountSql(new StringBuffer(sql));
                int totalCount = getCounts(ms, countSQL, boundSql,invocation,parameterObject);
                //获取分页后的结果
                final String pageSql = this.getMysqlPageSql(page, new StringBuffer(sql));
                ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
                page.setTotalCount(totalCount);
                Page.clearPage();
            }
        }
    }

    private void update(Invocation invocation) {
        final MappedStatement mappedStatement= (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType.UPDATE.equals(sqlCommandType) || sqlCommandType.INSERT.equals(sqlCommandType)) {
            if (parameter != null && parameter instanceof BaseEntity) {
                BaseEntity entity = (BaseEntity) parameter;
                JSONObject json = getUserBaseInfo(entity);
                Long id = json == null || json.getLong("id") == null ? 0 : json.getLong("id");
                String userName = json == null || StringUtils.isEmpty(json.getString("userName")) ? "system" : json.getString("userName");
                if (sqlCommandType.UPDATE.equals(sqlCommandType)) {
                    entity.setUpdateId(id);
                    entity.setUpdateTime(System.currentTimeMillis());
                    entity.setUpdateBy(userName);
                } else if (sqlCommandType.INSERT.equals(sqlCommandType)) {
                    entity.setCreatedId(id);
                    entity.setCreatedTime(System.currentTimeMillis());
                    entity.setCreatedBy(userName);
                }
            }
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
        sqlBuffer.append(" limit ").append(page.getPageNo()).append(",").append(page.getPageSize());
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
}
