package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

//影厅类型
@Data
public class HallTypeVO implements Serializable {
    private String halltypeId;
    private String halltypeName;
    private boolean isActive;
}
