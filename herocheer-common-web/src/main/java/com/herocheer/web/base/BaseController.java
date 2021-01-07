package com.herocheer.web.base;

import com.alibaba.fastjson.JSONObject;
import com.herocheer.common.base.entity.UserEntity;
import com.herocheer.common.constants.ResponseCode;
import com.herocheer.common.exception.CommonException;

import javax.servlet.http.HttpServletRequest;

/**
 * @desc 控制层辅助类
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
public class BaseController {
    /**
     * 获取当前登录用户的用户信息
     * 备注：使用@AllowAnonymous获取不到用户信息，会抛出异常
     * @param request
     * @return
     */
    protected UserEntity getUser(HttpServletRequest request){
        Object userBaseInfo = request.getAttribute("userBaseInfo");
        if(userBaseInfo == null){
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
        if(tokenId == null){
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
        if(userBaseInfo == null){
            throw new CommonException(ResponseCode.UN_LOGIN,"请先登录");
        }
        UserEntity userEntity = JSONObject.parseObject(userBaseInfo.toString(),UserEntity.class);
        return userEntity.getId();
    }

}
