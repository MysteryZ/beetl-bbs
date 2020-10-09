package com.ibeetl.bbs.dao;

import com.ibeetl.bbs.model.BbsReply;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Param;


import java.util.List;

public interface BbsReplyDao extends BaseMapper<BbsReply> {

    List<BbsReply> allReply(@Param("postId") Integer postId);

    void deleteByTopicId(@Param("topicId") int topicId);

    void deleteByPostId(@Param("postId") int postId);
}
