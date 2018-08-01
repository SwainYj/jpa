tsdb sdk 测试

获取设备列表  get  localhost:18080/device/list

回调接口     post  localhost:18080/stream/camEventHandler 
 
    param eg: type:Record  stream:swainswain  payload: {"alarming": true, "bucket":"live-bucket","key":"test5","start_time":1533104102000,"end_time":1533104122999,"format":"ts"}

获取录像列表  get  localhost:18080/stream/playlist?stream=swainswainswain&st=1532966400000&et=1533030341999


table : jpatest

    device:
      CREATE TABLE `base_device` (
        `deviceid` varchar(50) NOT NULL DEFAULT '',
        `uid` int(11) DEFAULT NULL,
        `desc` varchar(100) DEFAULT NULL,
        `connect_type` int(2) DEFAULT NULL,
        `status` int(4) DEFAULT NULL,
        PRIMARY KEY (`deviceid`)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
      
    user: 
      CREATE TABLE `t_user` (
        `t_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
        `t_name` varchar(45) DEFAULT NULL,
        `t_age` int(4) DEFAULT NULL,
        `t_address` varchar(120) DEFAULT NULL,
        `t_pwd` varchar(100) DEFAULT NULL,
        PRIMARY KEY (`t_id`)
      ) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;