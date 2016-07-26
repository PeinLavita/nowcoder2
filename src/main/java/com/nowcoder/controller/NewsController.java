package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.ToutiaoUtil;
import jdk.nashorn.internal.runtime.ECMAException;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yby on 2016/7/11.
 */

@Controller
public class NewsController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/addComment", method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setCreatedDate(new Date());
            comment.setContent(content);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setStatus(0);
            if (hostHolder.getUser() != null){
                comment.setUserId(hostHolder.getUser() .getId());
            }
            commentService.addComment(comment);

            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
        }catch (Exception e){
            logger.error("添加评论错误"+ e.getMessage());
        }

        return "redirect:/news/" + String.valueOf(newsId);
    }

    @RequestMapping(path = "/news/{newsId}", method = {RequestMethod.GET})
    //@ResponseBody
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){

        News news = newsService.getById(newsId);
        if (news != null){
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
            if (localUserId != 0) {
                model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                model.addAttribute("like", 0);
            }

            List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
            List<ViewObject> viewObjects = new ArrayList<>();
            for (Comment comment: comments){
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                viewObjects.add(vo);
            }
            model.addAttribute("comments", viewObjects);
        }
        model.addAttribute("news", news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail";
    }

    @RequestMapping(path = "/user/addNews", method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
        try {

            News news = new News();
            news.setCreatedDate(new Date());
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // 设置一个匿名用户
                news.setUserId(3);
            }
            newsService.addNews(news);
           // logger.info("......................................");
            return ToutiaoUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("资讯上传错误");
            return ToutiaoUtil.getJSONString(1,"发布失败");
        }
    }

    @RequestMapping(path = "/image", method = {RequestMethod.GET})
    @ResponseBody
    public void GetImage(@RequestParam("name") String imageName,
                           HttpServletResponse response){

        try{
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new
                    File(ToutiaoUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        }catch (Exception e){
            logger.error(e.getMessage());
            //return ToutiaoUtil.getJSONString(1, "读取图片失败");
        }

    }

    @RequestMapping(path = "/uploadImage/", method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
        try{

           // String fileUrl = newsService.saveImage(file);
            String fileUrl = qiniuService.saveImage(file);
            if (fileUrl == null){
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        }catch (Exception e){
            logger.error(e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传图片失败");
        }
    }
}
