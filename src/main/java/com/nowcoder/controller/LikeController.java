package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.RedisKeyUtil;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nowcoder.model.EntityType;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yby on 2016/7/20.
 */
@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    NewsService newsService;
    @Autowired
    LikeService likeService;
    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newId") int newsId){
        int userId = hostHolder.getUser().getId();

        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);
        newsService.updateLikeCount(newsId, (int)likeCount);

        //异步
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setEntityOwnerId(newsService.getById(newsId).getUserId())
                .setActorId(hostHolder.getUser().getId()).setEntityId(newsId));

        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = "/dislike", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newId") int newsId){
        int userId = hostHolder.getUser().getId();

        long likeCount = likeService.disLike(userId, EntityType.ENTITY_NEWS, newsId);
        newsService.updateLikeCount(newsId, (int)likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
