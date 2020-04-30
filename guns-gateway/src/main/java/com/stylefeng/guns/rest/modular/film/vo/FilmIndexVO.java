package com.stylefeng.guns.rest.modular.film.vo;

import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmIndexVO implements Serializable {
    //
    private List<BannerVO> banners;
    // 热映电影
    private FilmVO hotFilms;
    // 待上映电影
    private FilmVO soonFilms;
    private List<FilmInfo> boxRanking;
    private List<FilmInfo> expectRanking;
    // 排名前一百
    private List<FilmInfo> top100;
}
