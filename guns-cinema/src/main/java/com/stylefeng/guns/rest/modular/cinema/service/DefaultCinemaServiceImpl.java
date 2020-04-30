package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.persistence.dao.*;
import com.stylefeng.guns.rest.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceApi.class, executes = 10)
public class DefaultCinemaServiceImpl implements CinemaServiceApi {
    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;

    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;

    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;

    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;

    @Autowired
    private MoocFieldTMapper moocFieldTMapper;

    //1、根据CinemaQueryVO，查询影院列表
    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {
       /*
       判断是否传入条件 -> brandId,distId,hallType是否是99
        */
        //分页对象
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());
        //业务查询对象
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        //业务实体集合
        List<CinemaVO> cinemas = new ArrayList<>();
        if (cinemaQueryVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaQueryVO.getBrandId());
        }
        if (cinemaQueryVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id", cinemaQueryVO.getDistrictId());
        }
        if (cinemaQueryVO.getHallType() != 99) {
            entityWrapper.like("hall_ids", "%#" + cinemaQueryVO.getHallType() + "#%");
        }
        /*
        将数据实体转换为业务实体
        */
        List<MoocCinemaT> moocCinemas = moocCinemaTMapper.selectPage(page, entityWrapper);
        for (MoocCinemaT moocCinema : moocCinemas) {
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(moocCinema.getUuid() + "");
            cinemaVO.setAddress(moocCinema.getCinemaAddress());
            cinemaVO.setCinemaName(moocCinema.getCinemaName());
            cinemaVO.setMinimumPrice(moocCinema.getMinimumPrice() + "");
            cinemas.add(cinemaVO);
        }
        /*
        根据条件判断影院列表总数
         */
        long count = moocCinemaTMapper.selectCount(entityWrapper);
        Page<CinemaVO> result = new Page<>();
        //结果实体集合
        result.setRecords(cinemas);
        //每页条数
        result.setSize(cinemaQueryVO.getPageSize());
        //总数
        result.setTotal(count);
        return result;
    }

    //2、根据条件获取品牌列表[除了就99以外，其他的数字为isActive]
    @Override
    public List<BrandVO> getBrands(int brandId) {
        boolean flag = false;
        List<BrandVO> brands = new ArrayList<>();
        /*
        判空brandId brandId是否为99
         */
        MoocBrandDictT moocBrandDict = moocBrandDictTMapper.selectById(brandId);
        // 查询所有列表
        if (brandId == 99 || moocBrandDict == null || moocBrandDict.getUuid() == null) {
            flag = true;
        }
        List<MoocBrandDictT> moocBrandDicts = moocBrandDictTMapper.selectList(null);
        //判断flag如果为true 则将99置为isActive
        for (MoocBrandDictT brand : moocBrandDicts) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid() + "");
            if (flag) {
                if (brand.getUuid() == 99) {
                    brandVO.setActive(true);
                }
            } else {
                if (brand.getUuid() == brandId) {
                    brandVO.setActive(true);
                }
            }
            brands.add(brandVO);
        }
        return brands;
    }

    //3、获取行政区域列表
    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areas = new ArrayList<>();
        /*
        判空brandId brandId是否为99
         */
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
        // 查询所有列表
        if (areaId == 99 || moocAreaDictT == null || moocAreaDictT.getUuid() == null) {
            flag = true;
        }
        List<MoocAreaDictT> moocAreaDicts = moocAreaDictTMapper.selectList(null);
        //判断flag如果为true 则将99置为isActive
        for (MoocAreaDictT area : moocAreaDicts) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid() + "");
            if (flag) {
                if (area.getUuid() == 99) {
                    areaVO.setActive(true);
                }
            } else {
                if (area.getUuid() == areaId) {
                    areaVO.setActive(true);
                }
            }
            areas.add(areaVO);
        }
        return areas;
    }

    //4、获取影厅类型列表
    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> HallTypes = new ArrayList<>();
        /*
        判空brandId brandId是否为99
         */
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
        // 查询所有列表
        if (hallType == 99 || moocHallDictT == null || moocHallDictT.getUuid() == null) {
            flag = true;
        }
        List<MoocHallDictT> moocHallDictTs = moocHallDictTMapper.selectList(null);
        //判断flag如果为true 则将99置为isActive
        for (MoocHallDictT hallDict : moocHallDictTs) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeName(hallDict.getShowName());
            hallTypeVO.setHalltypeId(hallDict.getUuid() + "");
            if (flag) {
                if (hallDict.getUuid() == 99) {
                    hallTypeVO.setActive(true);
                }
            } else {
                if (hallDict.getUuid() == hallType) {
                    hallTypeVO.setActive(true);
                }
            }
            HallTypes.add(hallTypeVO);
        }
        return HallTypes;
    }

    //5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {
        /*
        查询数据实体
         */
        MoocCinemaT moocCinema = moocCinemaTMapper.selectById(cinemaId);
        /*
        将数据实体转换成业务实体
         */
        CinemaInfoVO cinemaInfo = new CinemaInfoVO();
        cinemaInfo.setCinemaId(moocCinema.getUuid() + "");
        cinemaInfo.setCinemaAddress(moocCinema.getCinemaAddress());
        cinemaInfo.setCinemaName(moocCinema.getCinemaName());
        cinemaInfo.setCinemaPhone(moocCinema.getCinemaPhone());
        cinemaInfo.setImgUrl(moocCinema.getImgAddress());
        return cinemaInfo;
    }

    //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfos = moocFieldTMapper.getFilmInfos(cinemaId);
        return filmInfos;
    }

    //7、根据放映场次ID获取放映信息
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {
        HallInfoVO hallInfoVO = moocFieldTMapper.getHallInfo(fieldId);
        return hallInfoVO;
    }

    //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        FilmInfoVO filmInfoVO = moocFieldTMapper.getFilmInfoById(fieldId);
        return filmInfoVO;
    }

    @Override
    public OrderQueryVO getOrderNeeds(int fieldId) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);
        orderQueryVO.setCinemaId(moocFieldT.getCinemaId() + "");
        orderQueryVO.setFilmPrice(moocFieldT.getPrice() + "");
        return orderQueryVO;
    }
}
