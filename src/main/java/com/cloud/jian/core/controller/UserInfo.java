package com.cloud.jian.core.controller;

import lombok.Data;

@Data
public class UserInfo {

    private String name;
    private String password;
    private Integer age;
    private String idCard;
    private String nickName;

    public String pringUserInfo() {
        System.out.println(name);
    }

}
