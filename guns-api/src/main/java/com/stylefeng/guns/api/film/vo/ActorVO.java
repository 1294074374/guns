package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActorVO implements Serializable {
    //演员图片
    private String imgAddress;
    //演员名字
    private String directorName;
    //角色名字
    private String roleName;

}
