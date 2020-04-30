package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film/")
public class FilmController {
    private static final String IMG_PRE = "www.meetingshop.cn";

    @Reference(interfaceClass = FilmServiceApi.class, check = false)
    private FilmServiceApi filmServiceApi;

    @Reference(interfaceClass = FilmAsyncServiceApi.class, async = true, check = false)
    private FilmAsyncServiceApi filmAsyncServiceApi;

    //获取首页接口
    /*
        API网关
            1.功能聚合【API聚合】
            好处：
                1. 六个接口，一次请求，同一时刻节省了五次HTTP请求
                2. 同一个接口对外暴漏，降低前后端分离开发的难度的复杂的
            坏处：
                1. 一次获取数据过多
     */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        //获取Banner信息
        List<BannerVO> banners = filmServiceApi.getBanners();
        //获取正在热映的电影
        FilmVO hotFilm = filmServiceApi.getHotFilms(true, 8, 1, 1, 99, 99, 99);
        //即将上映的电影
        FilmVO soonFilm = filmServiceApi.getSoonFilms(true, 8, 1, 1, 99, 99, 99);
        //票房排行榜
        List<FilmInfo> boxRanking = filmServiceApi.getBoxRanking();
        //获取受欢迎的榜单
        List<FilmInfo> expectRanking = filmServiceApi.getExpectRanking();
        //获取前一百
        List<FilmInfo> top = filmServiceApi.getTop();
        filmIndexVO.setBanners(banners);
        filmIndexVO.setHotFilms(hotFilm);
        filmIndexVO.setSoonFilms(soonFilm);
        filmIndexVO.setBoxRanking(boxRanking);
        filmIndexVO.setExpectRanking(expectRanking);
        filmIndexVO.setTop100(top);
        return ResponseVO.success(IMG_PRE, filmIndexVO);
    }

    /**
     * 影片条件列表查询接口
     *
     * @param catId    类型编号
     * @param sourceId 片源编号
     * @param yearId   年代编号
     * @return
     */
    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId", required = false, defaultValue = "99") String catId,
                                       @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
                                       @RequestParam(name = "yearId", required = false, defaultValue = "99") String yearId) {
        FilmConditionVO filmConditionVO = new FilmConditionVO();
        //类型集合
        List<CatVO> cats = filmServiceApi.getCats();
        List<CatVO> catResult = new ArrayList<>();
        CatVO cat = null;
        //标识位
        boolean flag = false;
        for (CatVO catVO : cats) {
            // 判断集合是否存在catId，如果存在，则将对应的实体变成active状态
            if (catVO.getCatId().equals("99")) {
                cat = catVO;
                continue;
            }
            if (catVO.getCatId().equals(catId)) {
                flag = true;
                catVO.setActive(true);
            } else {
                catVO.setActive(false);
            }
            catResult.add(catVO);
        }
        // 如果不存在，则默认将全部变为Active状态
        if (!flag) {
            cat.setActive(true);
            catResult.add(cat);
        } else {
            cat.setActive(false);
            catResult.add(cat);
        }
        //片源集合
        flag = false;
        List<SourceVO> sources = filmServiceApi.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO sourceVO = null;
        for (SourceVO source : sources) {
            if (source.getSourceId().equals("99")) {
                sourceVO = source;
                continue;
            }
            if (source.getSourceId().equals(catId)) {
                flag = true;
                source.setActive(true);
            } else {
                source.setActive(false);
            }
            sourceResult.add(source);

        }
        // 如果不存在，则默认将全部变为Active状态
        if (!flag) {
            sourceVO.setActive(true);
            sourceResult.add(sourceVO);
        } else {
            sourceVO.setActive(false);
            sourceResult.add(sourceVO);
        }
        //年代集合
        flag = false;
        List<YearVO> years = filmServiceApi.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        YearVO yearVO = null;
        for (YearVO year : years) {
            if (year.getYearId().equals("99")) {
                yearVO = year;
                continue;
            }
            if (year.getYearId().equals(catId)) {
                flag = true;
                year.setActive(true);
            } else {
                year.setActive(false);
            }
            yearResult.add(year);
        }
        // 如果不存在，则默认将全部变为Active状态
        if (!flag) {
            yearVO.setActive(true);
            yearResult.add(yearVO);
        } else {
            yearVO.setActive(false);
            yearResult.add(yearVO);
        }

        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);
        return ResponseVO.success(filmConditionVO);
    }

    /**
     * 影片查询接口
     */
    @RequestMapping(value = "getFilms", method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO) {
        String img_pre = "http://img.meetingshop.cn/";
        FilmVO filmVO = null;
        //根据showType查询影片类型
        switch (filmRequestVO.getShowType()) {
//            case 1: {
//                filmVO = filmServiceApi.getHotFilms(
//                        false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
//                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
//                        filmRequestVO.getCatId());
//                break;
//            }
            case 2: {
                filmVO = filmServiceApi.getSoonFilms(false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),
                        filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            }
            case 3: {
                filmVO = filmServiceApi.getClassicFilms(filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),
                        filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            }
            default: {
                filmVO = filmServiceApi.getHotFilms(
                        false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
            }
        }
        //根据sortId排序
        //添加各种条件查询
        //判断是第几页

        return ResponseVO.success(filmVO.getNowPage(), filmVO.getTotalPage(),img_pre,  filmVO.getFilmInfo());
    }

    @RequestMapping(value = "films/{searchParam}", method = RequestMethod.GET)
    public ResponseVO films(@PathVariable("searchParam") String searchParam,
                            int searchType) throws Exception {
        //根据searchType 判断查询类型
        FilmDetailVO filmDetail = filmServiceApi.getFilmDetails(searchType, searchParam);
        if (filmDetail == null) {
            return ResponseVO.serviceFail("没有可查询的影片");
        } else if (filmDetail.getFilmId() == null || filmDetail.getFilmId().trim().length() == 0) {
            return ResponseVO.serviceFail("没有可查询的影片");
        }
        String filmId = filmDetail.getFilmId();
        //查询影片的详细信息 -> dubbo 异步获取
        //获取影片描述信息
        //FilmDescVO filmDescVO = filmAsyncServiceApi.getFilmDesc(filmId);
        filmAsyncServiceApi.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescVOFuture = RpcContext.getContext().getFuture();
        //获取图片信息
        filmAsyncServiceApi.getImgs(filmId);
        Future<ImgVO> imgVOFuture = RpcContext.getContext().getFuture();
        //获取导演信息
        filmAsyncServiceApi.getDectInfo(filmId);
        Future<ActorVO> actorsVOFuture = RpcContext.getContext().getFuture();
        //获取演员信息
        filmAsyncServiceApi.getActors(filmId);
        Future<List<ActorVO>> actorsFuture = RpcContext.getContext().getFuture();

        InfoRequestVO infoRequestVO = new InfoRequestVO();
        ActorRequestVO actorRequestVO = new ActorRequestVO();
        actorRequestVO.setActors(actorsFuture.get());
        actorRequestVO.setDirector(actorsVOFuture.get());

        //组织info
        infoRequestVO.setActiors(actorRequestVO);
        infoRequestVO.setBiography(filmDescVOFuture.get().getBiography());
        infoRequestVO.setFilmId(filmId);
        infoRequestVO.setImgVO(imgVOFuture.get());

        filmDetail.setInfo04(infoRequestVO);
        return ResponseVO.success("http://img.meetingshop.cn/", filmDetail);
    }
}
