package com.herocheer.web.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.herocheer.cache.bean.RedisClient;
import com.herocheer.common.base.ResponseResult;
import com.herocheer.common.constants.ResponseCode;
import com.herocheer.common.utils.StringUtils;
import com.herocheer.web.annotation.AllowAnonymous;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @desc 登录验证拦截
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private RedisClient redisClient;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.interceptor(request, response, handler);
    }

    protected boolean interceptor(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(200);
            return false;
        } else {
            AllowAnonymous allowAnonymous = (AllowAnonymous)((HandlerMethod)handler).getMethod().getAnnotation(AllowAnonymous.class);
            if (allowAnonymous != null) {
                return true;
            }
            String authorization = request.getHeader("Authorization");
            if(StringUtils.isEmpty(authorization)){
                abortWith(response, ResponseCode.UN_LOGIN);
                return false;
            }
            String userInfo = redisClient.get(authorization);
            if (StringUtils.isEmpty(userInfo)) {
                abortWith(response,ResponseCode.UN_LOGIN);
                return false;
            }else {
                JSONObject json = JSONObject.parseObject(userInfo);
                request.setAttribute("userId",json.getLong("id"));
                request.setAttribute("phone",json.getString("phone"));
                request.setAttribute("certificateNo",json.getString("certificateNo"));
                request.setAttribute("userName",json.getString("userName"));
                return true;
            }
        }
    }

    private void abortWith(HttpServletResponse response, int code) {
        ResponseResult responseResult = ResponseResult.getResponse(code);
        responseResult.setMessage("您未登录");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;

        try {
            out = response.getWriter();
            out.append(JSON.toJSONString(responseResult));
            out.flush();
        } catch (IOException var7) {

        }finally {
            if(out != null){
                out.close();
            }
        }

    }
}
