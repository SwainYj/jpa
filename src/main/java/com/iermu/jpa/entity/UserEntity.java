package com.iermu.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * user : swain
 * 2018-07-26
 */

@Entity
@Table(name= "t_user")
public class UserEntity implements Serializable {
    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue
    @Column(name = "t_id")
    private Long id;

    @Column(name = "t_name")
    private  String name;

    @Column(name = "t_age")
    private  int age;

    @Column(name = "t_address")
    private String address;

    @Column(name = "t_pwd")
    private  String pwd;

    public Long getId(){
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }


}
