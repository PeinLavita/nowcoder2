package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by yby on 2016/7/3.
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
