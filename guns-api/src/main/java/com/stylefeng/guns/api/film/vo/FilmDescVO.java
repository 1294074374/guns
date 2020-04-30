package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmDescVO implements Serializable {
    //影片详情
    private String biography;
    //影片Id
    private String filmId;
}
