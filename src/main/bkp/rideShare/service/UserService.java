package com.test.service;

import com.test.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<String, User> userMap = new HashMap<>();
    public User addUser(String username,String sex,Integer age) throws  Exception{
        if(userMap.containsKey(username)){
            throw new Exception("User already exists");
        }
        User user = new User(username,sex,age);
        userMap.put(username,user);
        return user;
    }
    public User getUser(String userName){
        return userMap.getOrDefault(userName,null);
    }
    Collection<User> getAllUsers(){
        return userMap.values();
    }
}
