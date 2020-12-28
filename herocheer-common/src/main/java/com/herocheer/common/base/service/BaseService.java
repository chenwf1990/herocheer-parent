package com.herocheer.common.base.service;

import java.util.List;
import java.util.Map;

/**
 * @author chenwf
 * @desc
 * @date 2020/12/25
 * @company 厦门熙重电子科技有限公司
 */
public interface BaseService<T,ID> {
    /**
     * 根据主键id查询实体对象
     * @param id
     * @return
     */
    T get(ID id);

    /**
     * 根据请求参数查询对象集合
     * @param params
     * @return
     */
    List<T> findByLimit(Map<String, Object> params);

    /**
     * 新增
     * @param t
     * @return
     */
    Long insert(T t);


    long update(T t);
}
