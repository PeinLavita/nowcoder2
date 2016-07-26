package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by yby on 2016/7/3.
 */

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;


    public void addUser(User user){
        userDAO.addUser(user);
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashedMap();
        if (StringUtils.isEmpty(username)){
            map.put("msguser", "用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)){
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user != null){
            map.put("msgname", "用户名已被注册");
            return map;
        }

        user = new User(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));

        addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        //map.put()
        return map;

    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashedMap();
        if (StringUtils.isEmpty(username)){
            map.put("msguser", "用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)){
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user == null){
            map.put("msgname", "用户名不存在");
            return map;
        }
        if(!user.getPassword().equals(ToutiaoUtil.MD5(password+user.getSalt()))){
            map.put("msgpwd", "密码错误");
            return map;
        }

        //登陆成功
        map.put("userId", user.getId());

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;

    }

    public void logout(String ticket){

        loginTicketDAO.updateStatus(ticket,1);
    }


    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

}
