package com.nowcoder.service;

import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yby on 2016/7/3.
 */

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    public User getUser(int id) {
        return userDAO.selectById(id);
    }

}
