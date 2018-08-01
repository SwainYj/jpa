package com.iermu.jpa.controller;

import com.iermu.jpa.entity.UserEntity;
import com.iermu.jpa.jpa.UserJPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * user : swain
 */
@RestController
@RequestMapping(value = "user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserJPA userJPA;

    @RequestMapping(value = "/list")
    @ResponseBody
    public List<UserEntity> list(){
        return userJPA.findAll();
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public UserEntity save(UserEntity entity){
        logger.info("---user---param--"+entity.toString());
        return userJPA.save(entity);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public UserEntity update(UserEntity entity){
        logger.info("---user---param--"+entity.toString());
        return userJPA.saveAndFlush(entity);
    }

    @ResponseBody
    @RequestMapping(value = "/delete",produces = {"application/json;charset=UTF-8"})
    public Long delete(Long id){
        userJPA.deleteById(id);
        return id;
    }
}
