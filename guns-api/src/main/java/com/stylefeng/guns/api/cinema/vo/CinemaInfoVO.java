package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

//影院信息
@Data
public class CinemaInfoVO implements Serializable {
    private String cinemaId;
    private String imgUrl;
    private String cinemaName;
    private String cinemaAddress;
    private String cinemaPhone;
}
