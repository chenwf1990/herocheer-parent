package com.herocheer.common.base.service;

import com.herocheer.common.base.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author chenwf
 * @desc
 * @date 2020/12/25
 * @company 厦门熙重电子科技有限公司
 */
public interface BaseService<T extends BaseEntity,ID extends Serializable> {
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

    /**
     * 根据主键更新数据
     * @param t
     * @return
     */
    long update(T t);

    /**
     * 根据主键删除数据
     * @param ID
     * @return
     */
    long delete(ID ID);
}
