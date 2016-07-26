package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.Message;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by yby on 2016/7/21.
 */

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null){
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType eventType : eventTypes){
                    if (!config.containsKey(eventType)){
                        config.put(eventType, new ArrayList<EventHandler>());
                    }
                    config.get(eventType).add(entry.getValue());

                    //config.put(e)
                }
            }
        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(0, key);//阻塞等待0，
                    for (String message : messages){
                        if (message.equals(key)){
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())){
                            logger.error("不能识别");
                            continue;
                        }

                        List<EventHandler> eventHandlers = config.get(eventModel.getType());
                        for (EventHandler eventHandler : eventHandlers){
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


    }
}
