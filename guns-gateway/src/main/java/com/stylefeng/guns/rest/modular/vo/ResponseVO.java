package com.stylefeng.guns.rest.modular.vo;

import com.stylefeng.guns.api.film.vo.FilmVO;
import lombok.Data;

@Data
public class ResponseVO<M> {
    //返回状态 【0--成功，1--业务失败，999--系统异常】
    private int status;
    //返回信息
    private String msg;
    //返回数据实体
    private M data;
    //图片前缀
    private String imgPre;
    //当前页码
    private Integer nowPage;
    //总页数
    private Integer totalPage;

    private ResponseVO() {
    }

    public static <M> ResponseVO success(String imgPre, M m) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setData(m);
        responseVO.setImgPre(imgPre);
        return responseVO;
    }

    public static <M> ResponseVO success(M m) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setData(m);
        return responseVO;
    }

    public static <M> ResponseVO success(String msg) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <M> ResponseVO serviceFail(String msg) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(1);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <M> ResponseVO appFail(String msg) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(999);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static <M> ResponseVO success(Integer nowPage, Integer totalPage, String imgPre, M m) {
        ResponseVO responseVO = new ResponseVO();
        responseVO.setImgPre(imgPre);
        responseVO.setStatus(0);
        responseVO.setNowPage(nowPage);
        responseVO.setTotalPage(totalPage);
        responseVO.setData(m);
        return responseVO;
    }
}
