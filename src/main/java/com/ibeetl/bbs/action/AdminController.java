package com.ibeetl.bbs.action;

import com.alibaba.fastjson.JSONObject;
import com.ibeetl.bbs.common.WebUtils;
import com.ibeetl.bbs.es.annotation.EsEntityType;
import com.ibeetl.bbs.es.annotation.EsIndexType;
import com.ibeetl.bbs.es.annotation.EsOperateType;
import com.ibeetl.bbs.es.service.EsService;
import com.ibeetl.bbs.model.BbsPost;
import com.ibeetl.bbs.model.BbsReply;
import com.ibeetl.bbs.model.BbsTopic;
import com.ibeetl.bbs.model.BbsUser;
import com.ibeetl.bbs.service.BBSService;
import com.ibeetl.bbs.service.BbsUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author darren
 * @date 2019/10/25 9:38
 */
@Controller
@RequestMapping({"/bbs/admin", "/admin"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {


    WebUtils            webUtils;
    HttpServletRequest  request;
    HttpServletResponse response;
    BBSService          bbsService;
    BbsUserService      userService;
    SQLManager          sql;
    EsService           esService;


    @ResponseBody
    @PostMapping("/topic/nice/{id}")
    @EsIndexType(entityType = EsEntityType.BbsTopic, operateType = EsOperateType.UPDATE)
    public JSONObject editNiceTopic(@PathVariable int id) {
        JSONObject result = new JSONObject();
        if (!webUtils.isAdmin()) {
            //如果有非法使用，不提示具体信息，直接返回null
            result.put("err", 1);
            result.put("msg", "呵呵~~");
        } else {
            BbsTopic db   = bbsService.getTopic(id);
            Integer  nice = db.getIsNice();
            db.setIsNice(nice > 0 ? 0 : 1);
            bbsService.updateTopic(db);
            result.put("err", 0);
            result.put("msg", "success");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/topic/up/{id}")
    @EsIndexType(entityType = EsEntityType.BbsTopic, operateType = EsOperateType.UPDATE)
    public JSONObject editUpTopic(@PathVariable int id) {
        JSONObject result = new JSONObject();
        if (!webUtils.isAdmin()) {
            //如果有非法使用，不提示具体信息，直接返回null
            result.put("err", 1);
            result.put("msg", "呵呵~~");
        } else {
            BbsTopic db = bbsService.getTopic(id);
            Integer  up = db.getIsUp();
            db.setIsUp(up > 0 ? 0 : 1);
            bbsService.updateTopic(db);
            result.put("err", 0);
            result.put("msg", "success");
        }
        return result;
    }


    @ResponseBody
    @PostMapping("/topic/delete/{id}")
    @EsIndexType(entityType = EsEntityType.BbsTopic, operateType = EsOperateType.DELETE)
    public JSONObject deleteTopic(@PathVariable int id) {
        JSONObject result = new JSONObject();
        if (!webUtils.isAdmin()) {
            //如果有非法使用，不提示具体信息，直接返回null
            result.put("err", 1);
            result.put("msg", "呵呵~~");
        } else {
            bbsService.deleteTopic(id);
            result.put("err", 0);
            result.put("msg", "success");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/topic/deleteUser/{id}")
    @EsIndexType(entityType = EsEntityType.BbsTopic, operateType = EsOperateType.DELETE)
    public JSONObject deleteTopicOwner(@PathVariable int id) {
        JSONObject result = new JSONObject();
        if (!webUtils.isAdmin()) {
            //如果有非法使用，不提示具体信息，直接返回null
            result.put("err", 1);
            result.put("msg", "呵呵~~");
        } else {
            BbsTopic topic  = bbsService.getTopic(id);
            //防止管理员把自己给删除了
            if (!Objects.equals(webUtils.currentUser().getId(),topic.getUserId())){
                this.userService.removeUser(topic.getUserId());
            }
            result.put("err", 0);
            result.put("msg", "success");
        }
        return result;
    }

    @RequestMapping("/post/{p}")
    public String adminPosts(@PathVariable int p) {
        PageQuery query = new PageQuery(p);
        query.setPara("isAdmin", true);
        bbsService.getPosts(query);
        request.setAttribute("postPage", query);
        return "/bbs/admin/postList.html";
    }

    @RequestMapping("/post/edit/{id}")
    public String editPost(@PathVariable int id) {
        BbsPost post = sql.unique(BbsPost.class, id);
        request.setAttribute("post", post);
        request.setAttribute("topic", sql.unique(BbsTopic.class, post.getTopicId()));
        return "/postEdit.html";
    }

    /**
     * ajax方式编辑内容
     */
    @ResponseBody
    @RequestMapping("/post/update")
    @EsIndexType(entityType = EsEntityType.BbsPost, operateType = EsOperateType.UPDATE)
    public JSONObject updatePost(BbsPost post) {
        JSONObject result = new JSONObject();
        result.put("err", 1);
        if (post.getContent().length() < 10) {
            result.put("msg", "输入的内容太短，请重新编辑！");
        } else {
            BbsPost db = sql.unique(BbsPost.class, post.getId());
            if (canUpdatePost(db)) {
                db.setContent(post.getContent());
                bbsService.updatePost(db);
                result.put("id", post.getId());
                result.put("msg", "topic/" + db.getTopicId());
                result.put("err", 0);
            } else {
                result.put("msg", "不是自己发表的内容无法编辑！");
            }
        }
        return result;
    }

    /**
     * ajax方式删除内容
     */
    @ResponseBody
    @RequestMapping("/post/delete/{id}")
    @EsIndexType(entityType = EsEntityType.BbsPost, operateType = EsOperateType.DELETE)
    public JSONObject deletePost(@PathVariable int id) {
        JSONObject result = new JSONObject();
        BbsPost    post   = sql.unique(BbsPost.class, id);
        if (canUpdatePost(post)) {
            bbsService.deletePost(id);
            result.put("err", 0);
            result.put("msg", "删除成功！");
        } else {
            result.put("err", 1);
            result.put("msg", "不是自己发表的内容无法删除！");
        }
        return result;
    }


    @ResponseBody
    @PostMapping("/reply/delete/{id}")
    @EsIndexType(entityType = EsEntityType.BbsReply, operateType = EsOperateType.DELETE)
    public JSONObject deleteReply(@PathVariable int id) {

        JSONObject result = new JSONObject();
        if (canDeleteReply(id)) {
            bbsService.deleteReplay(id);
            result.put("err", 0);
            result.put("msg", "success");
        } else {
            result.put("err", 1);
            result.put("msg", "无法删除他人的回复");
        }
        return result;
    }


    /**
     * 初始化索引
     */
    @ResponseBody
    @RequestMapping("/es/init")
    public JSONObject initEsIndex() {
        JSONObject result = new JSONObject();
        if (!webUtils.isAdmin()) {
            //如果有非法使用，不提示具体信息，直接返回null
            result.put("err", 1);
            result.put("msg", "呵呵~~");
        } else {
            esService.initIndex();
            result.put("err", 0);
            result.put("msg", "ES初始化成功");
        }
        return result;
    }


    private boolean canDeleteReply(Integer replyId) {

        BbsUser  user  = this.webUtils.currentUser();
        BbsReply reply = bbsService.getReply(replyId);
        if (reply.getUserId().equals(user.getId())) {
            return true;
        }
        //如果是admin
        return user.getUserName().equals("admin");
    }

    private boolean canUpdatePost(BbsPost post) {

        BbsUser user = this.webUtils.currentUser();
        if (post.getUserId().equals(user.getId())) {
            return true;
        }
        //如果是admin
        return user.getUserName().equals("admin");
    }
}
