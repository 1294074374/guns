package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaQueryVO implements Serializable {
    //影院编号	否,默认为99，全部
    private int brandId = 99;
    //	影厅类型	否,默认为99，全部
    private int hallType = 99;
    //	行政区编号	否,默认为99，全部
    private int districtId = 99;
    //每页条数	否,默认为12条
    private int pageSize = 12;
    //当前页数	否,默认为第1页
    private int nowPage = 1;
}
