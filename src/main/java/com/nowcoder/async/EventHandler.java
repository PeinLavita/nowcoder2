package com.nowcoder.async;

import java.util.List;

/**
 * Created by yby on 2016/7/21.
 */



public interface EventHandler {

    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();

}
