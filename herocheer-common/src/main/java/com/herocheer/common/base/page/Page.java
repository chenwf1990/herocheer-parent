package com.herocheer.common.base.page;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * @author chenwf
 * @desc
 * @date 2020/12/26
 * @company 厦门熙重电子科技有限公司
 */
public class Page<T> implements Serializable {
    protected static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal();

    private Integer totalPage;
    private Integer totalCount;
    private Integer pageSize = 10;
    private Integer pageNo = 1;
    private List<T> dataList;

    public Page(Integer pageNo, Integer pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Page(String json){
        try {
            JSONObject obj = JSONObject.parseObject(json);
            if (obj != null) {
                this.pageNo = obj.getIntValue("pageNo");
                this.pageSize = obj.getIntValue("pageSize");
            }
        } catch (Exception e) {
        }
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalPage = totalCount / pageSize;
        Integer plus = 0;
        if(totalCount % pageSize  > 0){
            plus = 1;
        }
        this.totalPage += plus;
        this.totalCount = totalCount;
    }

    protected static void setLocalPage(Page page) {
        LOCAL_PAGE.set(page);
    }

    public static <T> Page<T> getLocalPage() {
        return (Page)LOCAL_PAGE.get();
    }

    public static void clearPage() {
        LOCAL_PAGE.remove();
    }

    public static <E> Page<E> startPage(int pageNo, int pageSize) {
        Page<E> page = new Page(pageNo, pageSize);
        setLocalPage(page);
        return page;
    }
}
