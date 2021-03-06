package com.herocheer.mybatis.base.dao;

import com.herocheer.common.base.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @desc dao辅助类
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
public interface BaseDao<T extends BaseEntity,ID extends Serializable> {
    T get(ID id);

    List<T> findByLimit(Map<String, Object> params);

    int insert(T t);

    int update(T t);

    int delete(ID id);

    int count(Map<String, Object> params);
}
