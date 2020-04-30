package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceApi {
    /*
    ==== 获取首页信息接口 ====
     */
    //获取banners
    List<BannerVO> getBanners();

    //获取热映影片
    FilmVO getHotFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    //获取几件上映影片【受欢迎程度排序】
    FilmVO getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    // 获取经典影片
    FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    //获取票房排行榜
    List<FilmInfo> getBoxRanking();

    //获取人气排行榜
    List<FilmInfo> getExpectRanking();

    //获取Top100
    List<FilmInfo> getTop();

    /*
    ==== 获取影片条件接口 ====
     */
    //获取影片类型
    List<CatVO> getCats();

    //获取片源条件
    List<SourceVO> getSources();

    //获取年代条件
    List<YearVO> getYears();

    /*
    ==== 影片详情查询接口 ====
     */
    //根据影片Id或者影片名称获取影片详情信息
    FilmDetailVO getFilmDetails(int searchType, String searchParam);

    //获取影片描述信息
    FilmDescVO getFilmDesc(String filmId);

    //获取图片信息
    ImgVO getImgs(String filmId);

    //获取导演信息
    ActorVO getDectInfo(String filmId);

    //获取演员信息
    List<ActorVO> getActors(String filmId);
}
