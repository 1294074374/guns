package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmInfo implements Serializable {

    private String filmId;
    private String filmType;
    private String imgAddress;
    private String filmName;
    private String filmScore;
    private String showTime;
    private int expectNum;
    private String showNum;
    private int boxNum;
    private String score;
}
