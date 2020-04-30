package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data

public class InfoRequestVO implements Serializable {
    private String biography;
    private ActorRequestVO actiors;
    private ImgVO imgVO;
    private String filmId;
}
