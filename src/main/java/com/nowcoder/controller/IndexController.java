package com.nowcoder.controller;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.nowcoder.model.User;
import com.nowcoder.service.toutiaoService;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by yby on 2016/6/27.
 */

//@Controller
public class IndexController {

    @Autowired
    private toutiaoService toutiaoServicem;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String index(HttpSession session) {
        //logger.info("Visit Index");
        return "Hello NowCoder," + session.getAttribute("msg")
                + "<br> Say:" + toutiaoServicem.say();
    }

//    @RequestMapping(path = {"/", "/index"})
//    @ResponseBody
//    public String index(){
//        return "hello nowcoder";
//    }

    @RequestMapping(path = "/profile/{groupId}/{userId}")
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "nowcoder") String key){
        return String.format("%s, %d, %d, %s", groupId, userId, type, key);
    }

    @RequestMapping(value = "/vm")
    public String news(Model model){


        List<String> colors = Arrays.asList(new String[]{"red", "green", "yellow"});
        Map<String, String> map = new HashedMap();
        for (int i = 0; i < 4; ++i) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }

        model.addAttribute("value1", "test");
        model.addAttribute("colors", colors);
        model.addAttribute("map", map);
        model.addAttribute("user", new User("jill"));

        return "news";
    }

    @RequestMapping(path = "/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse reponse,
                          HttpSession session){

        StringBuilder sb = new StringBuilder();
        Enumeration<String> headernames = request.getHeaderNames();
        while(headernames.hasMoreElements()){
            String name = headernames.nextElement();
            sb.append("headernames: " + name + "<br>");

            sb.append("header: " + request.getHeader(name) + "<br>");

        }

        for (Cookie cookie : request.getCookies()) {
            sb.append("cookie: " + cookie.getName() + cookie.getValue() + "<br>");
        }

        sb.append("method: " + request.getMethod());

        return sb.toString();
    }
    @RequestMapping("/response")
    @ResponseBody
    public String response(@CookieValue(value ="nowcoderId", defaultValue = "none") String nowcoderId,
                           @RequestParam(value = "key", defaultValue = "keyDef") String key,
                           @RequestParam(value = "value", defaultValue = "valueDef") String value,
                           HttpServletResponse response){

        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);

        return "nowcoderId:  " + nowcoderId;
    }

    @RequestMapping("/redirect/{code}")
    public String redirect(@PathVariable("code") int code,
                           HttpSession session){
//        RedirectView red = new RedirectView("/", true);
//        if (code == 301){
//            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//        }
//        return red;

        session.setAttribute("msg", "Jump from redirect.");
        return "/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key1){
        if (key1.equals("admin")){
            return "hello admin";
        }
        throw new IllegalArgumentException("key error");
    }

@ExceptionHandler
    @ResponseBody
    public String error(Exception e){
    return "error"+ e.getMessage();
}

}
