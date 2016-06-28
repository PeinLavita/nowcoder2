package com.nowcoder.model;

/**
 * Created by yby on 2016/6/28.
 */
public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    public User(String str){
        name = str;
    }

}
