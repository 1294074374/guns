package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/cinema/")
public class CinemaController {
    @Reference(interfaceClass = CinemaServiceApi.class, cache = "lru", check = false, connections = 10)
    private CinemaServiceApi cinemaServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class, check = false)
    private OrderServiceApi orderServiceApi;
    private static final String IMG_PRE = "http://img.meeetingshop.cn/";

    //1、查询影院列表-根据条件查询所有影院
    @RequestMapping(value = "getCinemas", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getCinemas(CinemaQueryVO cinemaQueryVO) {
        try {
            //根据五个条件查询
            Page<CinemaVO> cinemas = cinemaServiceApi.getCinemas(cinemaQueryVO);
            //判断是否有满足条件的影院
            if (cinemas.getRecords() == null || cinemas.getRecords().size() == 0) {
                return ResponseVO.success("没有影院可查");
            } else {
                return ResponseVO.success(cinemas.getCurrent(), (int) cinemas.getPages(), "", cinemas.getRecords());
            }
        } catch (Exception e) {
            log.error("获取影院信息异常:" + e);
            return ResponseVO.serviceFail("获取影院列表失败");
        }
    }

    // 获取影院列表查询条件
    @RequestMapping(value = "getCondition", method = RequestMethod.GET)
    @ResponseBody
    public ResponseVO getCondition(CinemaQueryVO cinemaQueryVO) {
        try {
            List<BrandVO> brands = cinemaServiceApi.getBrands(cinemaQueryVO.getBrandId());
            List<AreaVO> areas = cinemaServiceApi.getAreas(cinemaQueryVO.getDistrictId());
            List<HallTypeVO> hallTypes = cinemaServiceApi.getHallTypes(cinemaQueryVO.getHallType());
            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setAreaList(areas);
            cinemaConditionResponseVO.setHalltypeList(hallTypes);
            return ResponseVO.success(cinemaConditionResponseVO);
        } catch (Exception e) {
            log.error("获取影院列表查询条件异常:" + e);
            return ResponseVO.serviceFail("获取影院列表查询条件失败");
        }
    }

    // 获取影院列表查询条件
    @RequestMapping(value = "getFields")
    @ResponseBody
    public ResponseVO getFields(Integer cinemaId) {
        try {
            CinemaInfoVO cinemaInfoById = cinemaServiceApi.getCinemaInfoById(cinemaId);
            List<FilmInfoVO> filmInfoByCinemaId = cinemaServiceApi.getFilmInfoByCinemaId(cinemaId);
            CinemaFieldResponseVO cinemaFieldResponseVO = new CinemaFieldResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmList(filmInfoByCinemaId);
            return ResponseVO.success(IMG_PRE, cinemaFieldResponseVO);
        } catch (Exception e) {
            log.error("获取播放场次异常:" + e);
            return ResponseVO.serviceFail("获取播放场次失败");
        }
    }

    // 获取场次详细信息接口
    @RequestMapping(value = "getFieldInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVO getFieldInfo(Integer cinemaId, Integer fieldId) {
        try {
            CinemaInfoVO cinemaInfoById = cinemaServiceApi.getCinemaInfoById(cinemaId);
            FilmInfoVO filmInfoByFieldId = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
            HallInfoVO filmFieldInfo = cinemaServiceApi.getFilmFieldInfo(fieldId);

            filmFieldInfo.setSoldSeats(orderServiceApi.getSoldSeatsByFieldId(fieldId));

            CinemaFieldsResponseVO cinemaFieldsResponseVO = new CinemaFieldsResponseVO();
            cinemaFieldsResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldsResponseVO.setFilmInfo(filmInfoByFieldId);
            cinemaFieldsResponseVO.setHallInfo(filmFieldInfo);
            return ResponseVO.success(IMG_PRE, cinemaFieldsResponseVO);
        } catch (Exception e) {
            log.error("获取场次详细信息异常:" + e);
            return ResponseVO.serviceFail("获取场次详细信息失败");
        }
    }
}
