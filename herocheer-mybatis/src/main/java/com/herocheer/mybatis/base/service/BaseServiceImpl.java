package com.herocheer.mybatis.base.service;

import com.herocheer.common.base.entity.BaseEntity;
import com.herocheer.common.base.service.BaseService;
import com.herocheer.common.exception.CommonException;
import com.herocheer.mybatis.base.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @desc 辅助类
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
public abstract class BaseServiceImpl<D extends BaseDao<T,ID>,T extends BaseEntity,ID extends Serializable> implements BaseService<T,ID> {
    @Autowired
    protected D dao;

    @Override
    public T get(ID id) {
        return this.dao.get(id);
    }

    /**
     * 根据请求参数查询对象集合
     * @param params
     * @return
     */
    @Override
    public List<T> findByLimit(Map<String,Object> params){
        List<T> record = this.dao.findByLimit(params);
        if(!record.isEmpty() && record.size() > 1000){
            throw new CommonException("more than 1000");
        }
        return record;
    }

    /**
     * 新增
     * @param t
     * @return
     */
    @Override
    public Long insert(T t){
        return this.dao.insert(t);
    }

    @Override
    public long update(T t) {
        return this.dao.update(t);
    }

    /**
     * 根据主键删除数据
     *
     * @param id
     * @return
     */
    public long delete(ID id) {
        return this.dao.delete(id);
    }
}
