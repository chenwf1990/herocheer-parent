package com.herocheer.web.base;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.herocheer.cache.bean.RedisClient;
import com.herocheer.common.base.entity.UserEntity;
import com.herocheer.common.constants.ResponseCode;
import com.herocheer.common.exception.CommonException;
import com.herocheer.common.utils.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @desc 控制层辅助类
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
public class BaseController {
    @Resource
    private RedisClient redisClient;
    /**
     * 获取当前登录用户的用户信息
     * 备注：使用@AllowAnonymous获取不到用户信息，会抛出异常
     * @param request
     * @return
     */
    protected UserEntity getUser(HttpServletRequest request){
        Object userBaseInfo = request.getAttribute("userBaseInfo");
        if(ObjectUtil.isEmpty(userBaseInfo)){
            throw new CommonException(ResponseCode.UN_LOGIN,"请先登录");
        }
        UserEntity userEntity = JSONObject.parseObject(userBaseInfo.toString(),UserEntity.class);
        return userEntity;
    }

    /**
     * 获取当前tokenId
     * 备注：使用@AllowAnonymous获取不到用户信息，会抛出异常
     * @param request
     * @return
     */
    protected String getCurTokenId(HttpServletRequest request){
        Object tokenId = request.getHeader("authorization");
        if(ObjectUtil.isEmpty(tokenId)){
            return "";
        }
        return tokenId.toString();
    }

    /**
     * 获取当前登录用户的用户信息
     * 备注：使用@AllowAnonymous获取不到用户信息，会抛出异常
     * @param request
     * @return
     */
    protected Long getCurUserId(HttpServletRequest request){
        Object userBaseInfo = request.getAttribute("userBaseInfo");
        if(ObjectUtil.isEmpty(userBaseInfo)){
            throw new CommonException(ResponseCode.UN_LOGIN,"请先登录");
        }
        UserEntity userEntity = JSONObject.parseObject(userBaseInfo.toString(),UserEntity.class);
        return userEntity.getId();
    }

    /**
     * 限制接口频繁调用，导致并发操作
     * key+userId = key
     * @param request
     * @param key
     * @param timeOut
     */
    protected void reqLimitByUserId(HttpServletRequest request,String key,Integer timeOut){
        Object userBaseInfo = request.getAttribute("userBaseInfo");
        if(ObjectUtil.isNotEmpty(userBaseInfo)){
            UserEntity userEntity = JSONObject.parseObject(userBaseInfo.toString(),UserEntity.class);
            key += "_" + userEntity.getId();
        }
        boolean flag = redisClient.setnx(key, "", timeOut);
        if(!flag){
            throw new CommonException("请勿频繁操作");
        }
    }

    /**
     * 限制接口频繁调用，导致并发操作
     * @param key
     * @param timeOut
     */
    protected void reqLimit(String key,Integer timeOut){
        boolean flag = redisClient.setnx(key, "", timeOut);
        if(!flag){
            throw new CommonException("请勿频繁操作");
        }
    }

}
