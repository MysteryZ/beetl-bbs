package com.ibeetl.bbs.es.service;

import org.beetl.sql.core.engine.PageQuery;

import com.ibeetl.bbs.es.annotation.EntityType;
import com.ibeetl.bbs.es.annotation.EsOperateType;
import com.ibeetl.bbs.es.entity.BbsIndex;
import com.ibeetl.bbs.es.vo.IndexObject;


public interface SearchService {

    /**
     * 公共操作方法
     */

    public void editEsIndex(EntityType entityType, EsOperateType operateType, Object id);

    public void editEsIndexFallback(EntityType entityType, EsOperateType operateType, Object id) ;

    /**
     * 重构索引
     */
    public void initIndex() ;

    public void initIndexFallback() ;

    /**
     * 创建索引对象
     */
    public BbsIndex createBbsIndex(EntityType entityType, Integer id) ;

    /**
     * 创建所有并返回搜索结果
     */
    public PageQuery<IndexObject> getQueryPage(String keyword, int p) ;

    public PageQuery<IndexObject> getQueryPageFallback(String keyword, int p);


}
