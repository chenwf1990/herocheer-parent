package com.herocheer.mybatis.base;

import java.util.List;
import java.util.Map;

/**
 * @desc dao辅助类
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
public interface BaseDao<T,ID> {
    T get(ID id);

    List<T> findByLimit(Map<String, Object> params);

    Long insert(T t);

    long update(T t);
}
