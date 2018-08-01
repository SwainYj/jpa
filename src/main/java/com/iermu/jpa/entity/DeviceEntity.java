package com.iermu.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name= "base_device")
public class DeviceEntity implements Serializable{
    @Id
    @Column(name = "deviceid")
    private String deviceid;

    @Column(name = "uid")
    private  Integer uid;

    @Column(name = "desc")
    private  String desc;

    @Column(name = "connect_type")
    private Integer connect_type;

    @Column(name = "status")
    private  Integer status;

    public String getDeviceid(){
        return deviceid;
    }
    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public Integer getUid() {
        return uid;
    }
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String  getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc == null ? null : desc.trim();
    }

    public Integer getConnect_type() {
        return connect_type;
    }
    public void setConnect_type(Integer connect_type) {
        this.connect_type = connect_type == null ? 0 : connect_type;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status == null ? 0 : status;
    }
}
