package com.nowcoder.async;

/**
 * Created by yby on 2016/7/21.
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;
    EventType(int value){
        this.value = value;
    }
}
