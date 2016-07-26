package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yby on 2016/7/13.
 */

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @RequestMapping(value = "/msg/addMessage", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setContent(content);
        message.setFromId(fromId);
        message.setToId(toId);
        message.setConversationId( toId > fromId ? (String.format("%d_%d",fromId,toId)) : (String.format("%d_%d",toId, fromId)));

        messageService.addMessage(message);
        return ToutiaoUtil.getJSONString(message.getId());
    }

    @RequestMapping(path = "/msg/detail", method = {RequestMethod.GET})
    public String conversationDetail(Model model, @RequestParam("conversationId") String conversationId){
      try{

          List<Message> messages = messageService.getConversationDetail(conversationId, 0, 10);
          List<ViewObject> viewObjects = new ArrayList<>();

          for (Message message : messages){
              ViewObject viewObject = new ViewObject();
              viewObject.set("message", message);
              User user = userService.getUser(message.getFromId());
              if (user == null){
                  continue;
              }
              viewObject.set("headUrl", user.getHeadUrl());
              viewObject.set("userName", user.getName());

              viewObjects.add(viewObject);
          }

          model.addAttribute("messages", viewObjects);
          return "letterDetail";

      }catch(Exception e){
          logger.error("fail", e.getMessage());
      }

        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model){

        if (hostHolder.getUser() == null){
            return "/?pop=1";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> messages = messageService.getConversationList(localUserId, 0, 10);
        List<ViewObject> viewObjects = new ArrayList<>();
        for (Message message : messages){
            ViewObject viewObject = new ViewObject();
            int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
            User user = userService.getUser(targetId);
            if (user == null){
                continue;
            }
           viewObject.set("conversation", message);
           viewObject.set("headUrl", user.getHeadUrl());
           viewObject.set("userName", user.getName());
           viewObject.set("targetId", targetId);
           viewObject.set("totalCount", message.getId());
           viewObject.set("unreadCount", messageService.getUnreadCount(localUserId, message.getConversationId()));

            viewObjects.add(viewObject);
        }


        model.addAttribute("conversations", viewObjects);
        return "letter";
    }
}
