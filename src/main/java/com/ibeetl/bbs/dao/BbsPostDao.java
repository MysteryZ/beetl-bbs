package com.ibeetl.bbs.dao;

import com.ibeetl.bbs.model.BbsPost;

import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Param;
import org.beetl.sql.mapper.annotation.Sql;

import java.util.Date;

public interface BbsPostDao extends BaseMapper<BbsPost> {

    void getPosts(PageQuery query);

    void deleteByTopicId(@Param("topicId") int topicId);

    @Sql(value = "select max(create_time) from bbs_post where user_id=? order by id desc ")
    Date getLatestPostDate(int userId);

}
