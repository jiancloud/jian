package com.cloud.jian.core.controller;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
public class UserInfo {

    private String name;
    private String password;
    private Integer age;
    private String idCard;
    private String nickName;

}
