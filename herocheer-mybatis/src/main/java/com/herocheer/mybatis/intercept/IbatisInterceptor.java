package com.herocheer.mybatis.intercept;

import cn.hutool.core.util.ReflectUtil;
import com.herocheer.common.base.entity.BaseEntity;
import com.herocheer.common.base.page.Page;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.springframework.stereotype.Component;

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
//        @Signature(type = Executor.class,method = "update",args = {MappedStatement.class, Object.class}),
        @Signature(type = StatementHandler.class,method = "prepare",args = {Connection.class,Integer.class})
})
public class IbatisInterceptor implements Interceptor {
    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget(); //获取分页拦截器必要的参数
        final StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        //获取StateMent
        final MappedStatement mappedStatement=(MappedStatement)ReflectUtil.getFieldValue(delegate, "mappedStatement");
        //获取执行的SQL
        final BoundSql boundSql = delegate.getBoundSql();
        //获取执行的参数
        final Object parameter = boundSql.getParameterObject();
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType.UPDATE.equals(sqlCommandType)) {
            if (parameter != null && parameter instanceof BaseEntity) {
                BaseEntity entity = (BaseEntity) parameter;
                entity.setUpdateId(0L);
                entity.setUpdateTime(System.currentTimeMillis());
                entity.setUpdateBy("chenwf");
            }
        }else if (sqlCommandType.INSERT.equals(sqlCommandType)) {
            if (parameter != null && parameter instanceof BaseEntity) {
                BaseEntity entity = (BaseEntity) parameter;
                entity.setCreatedId(0L);
                entity.setCreatedTime(System.currentTimeMillis());
                entity.setCreatedBy("chenwf");
            }
        }else if (sqlCommandType.SELECT.equals(sqlCommandType)) {
            //判断参数是否有Page参数，如果有则按照
            Page page = Page.getLocalPage();
            if (page != null) {
                final String sql = boundSql.getSql();
                //计算总的条目数
                final String countSQL = getMysqlCountSql(new StringBuffer(sql));
                int totalCount = getCounts(mappedStatement, countSQL, boundSql, page,invocation);
                //获取分页后的结果
                final String pageSql = this.getMysqlPageSql(page, new StringBuffer(sql));
                ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
                page.setTotalCount(totalCount);
                Page.clearPage();
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
      * @param invocation */
     private int getCounts(MappedStatement mappedStatement, String countSQL, BoundSql boundSql, Page page, Invocation invocation) {
         final ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, page, boundSql);
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
