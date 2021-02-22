package com.herocheer.cache.aspect;

import com.alibaba.fastjson.JSONObject;
import com.herocheer.cache.annotation.RedisCache;
import com.herocheer.cache.bean.RedisClient;
import com.herocheer.common.base.ResponseResult;
import com.herocheer.common.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chenwf
 * @desc
 * @date 2021/2/2
 * @company 厦门熙重电子科技有限公司
 */
@Order(-1)
@Aspect
@Component
public class RedisAspect {
    @Resource
    private RedisClient redisClient;

    @Around("@annotation(redisCache)")
    public Object processor(ProceedingJoinPoint pjp, RedisCache redisCache) throws Throwable {
        String cacheKey = generationCacheKey(pjp,redisCache);
        final String cacheResult = redisClient.get(cacheKey);
        if (!StringUtils.isEmpty(cacheResult)) {
            return ResponseResult.ok(JSONObject.parseObject(cacheResult));
        }
        //执行方法
        final Object proceed = pjp.proceed();
        String result = JSONObject.toJSONString(proceed);
        JSONObject json = JSONObject.parseObject(result);
        redisClient.set(cacheKey,json.getString("data"),redisCache.expire());
        return proceed;
    }

    private String generationCacheKey(ProceedingJoinPoint pjp,RedisCache redisCache) {
        final String key = redisCache.key();
        Class clazz = pjp.getTarget().getClass();
        String className = clazz.getName();
        Object[] args = pjp.getArgs();
        String argJson = JSONObject.toJSONString(args);
        String cacheKey;
        if(StringUtils.isEmpty(key)){
            cacheKey = className + "&&&" + argJson;
        }else if (key.contains("#")){
            cacheKey = key + "&&&" + className + "&&&" + argJson;
        }else {
            cacheKey = key;
        }
        return cacheKey;
    }
}
