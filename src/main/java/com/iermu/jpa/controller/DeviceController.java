package com.iermu.jpa.controller;

import com.iermu.jpa.entity.DeviceEntity;
import com.iermu.jpa.jpa.DeviceJPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * user : swain
 */
@RestController
@RequestMapping(value = "device")
public class DeviceController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private DeviceJPA deviceJPA;

    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(){
        List<DeviceEntity> list = deviceJPA.findAll();
        Integer count = list.size();

        Object object = new DeviceListResult(); //deviceJPA.findAll();
        ((DeviceListResult) object).list = list;
        ((DeviceListResult) object).count = count;
        return object;
    }
}

class DeviceListResult{
    public int count;
    public List<DeviceEntity> list;
    }
