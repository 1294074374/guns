package com.stylefeng.guns.rest.modular.order.service;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.api.util.UUIDUitl;
import com.stylefeng.guns.rest.persistence.dao.MoocOrder2017TMapper;
import com.stylefeng.guns.rest.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.persistence.model.MoocOrder2018T;
import com.stylefeng.guns.rest.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceApi.class, group = "order2017")
public class OrderServiceImpl2017 implements OrderServiceApi {
    @Autowired
    private MoocOrder2017TMapper moocOrder2017TMapper;

    @Reference(interfaceClass = CinemaServiceApi.class, check = false)
    private CinemaServiceApi cinemaServiceApi;

    @Autowired
    private FTPUtil ftpUtil;

    // 验证售出的票是否为真
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        // 根据FieldId 找到位置图
        String seatsPath = moocOrder2017TMapper.getSeatsByFieldId(fieldId);
        // 读取位置图像
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatsPath);
        // 将图像文件转为json
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);
        String ids = jsonObject.get("ids").toString();
        // 每一次匹配上的，都给isTrue+1
        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(",");
        int isTrue = 0;
        for (String id : idArrs) {
            for (String seat : seatArrs) {
                if (id.equalsIgnoreCase(seat)) {
                    isTrue++;
                }
            }
        }
        if (seatArrs.length == isTrue) {
            return true;
        } else {
            return false;
        }
    }

    // 已经销售的座位里，有没有这些座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id", fieldId);

        List<MoocOrder2017T> list = moocOrder2017TMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");
        // 但凡有一个编号匹配上，则返回失败
        for (MoocOrder2017T moocOrderT : list) {
            String[] ids = moocOrderT.getSeatsIds().split(",");
            for (String id : ids) {
                for (String seat : seatArrs) {
                    if (id.equalsIgnoreCase(seat)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 创建订单信息
    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        // 编号
        String uuid = UUIDUitl.getUuid();

        // 影片信息
        FilmInfoVO filmInfoVO = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.valueOf(filmInfoVO.getFilmId());

        //获取影院信息
        OrderQueryVO orderQueryVO = cinemaServiceApi.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        Double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());
        //订单总金额
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds, filmPrice);

        MoocOrder2017T moocOrder2017T = new MoocOrder2017T();
        moocOrder2017T.setUuid(uuid);
        moocOrder2017T.setSeatsName(seatsName);
        moocOrder2017T.setSeatsIds(soldSeats);
        moocOrder2017T.setOrderUser(userId);
        moocOrder2017T.setOrderPrice(totalPrice);
        moocOrder2017T.setFilmPrice(filmPrice);
        moocOrder2017T.setFieldId(fieldId);
        moocOrder2017T.setFilmId(filmId);
        moocOrder2017T.setCinemaId(cinemaId);

        Integer insert = moocOrder2017TMapper.insert(moocOrder2017T);
        if (insert > 0) {
            OrderVO orderVO = moocOrder2017TMapper.getOrderInfoById(uuid);
            if (orderVO == null || orderVO.getOrderId() == null) {
                log.error("订单信息查询失败，订单编号为：" + uuid);
                return null;
            } else {
                return orderVO;
            }
        } else {
            log.error("订单插入失败");
            return null;
        }
    }

    private static double getTotalPrice(int solds, double filmPrice) {
        BigDecimal soldsDecimal = new BigDecimal(solds);
        BigDecimal filmPriceDecimal = new BigDecimal(filmPrice);
        BigDecimal result = soldsDecimal.multiply(filmPriceDecimal);
        // 四舍五入取小数点后两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }

    // 使用当前登陆人获取已经购买的订单
    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        if (userId == null) {
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        } else {
            List<OrderVO> ordersByUserId = moocOrder2017TMapper.getOrdersByUserId(userId, page);
            if (ordersByUserId == null && ordersByUserId.size() == 0) {
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            } else {
                // 获取订单总数
                EntityWrapper<MoocOrder2017T> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user", userId);
                Integer counts = moocOrder2017TMapper.selectCount(entityWrapper);
                // 将结果放入Page
                result.setTotal(counts);
                result.setRecords(ordersByUserId);

                return result;
            }
        }
    }

    // 根据FieldId 获取所有已经销售的座位编号
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if (fieldId == null) {
            log.error("未传入已售查询场次失败，业务查询失败");
            return "";
        } else {
            String result = moocOrder2017TMapper.getSoldSeatsByFieldId(String.valueOf(fieldId));
            return result;
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {

        OrderVO orderInfoById = moocOrder2017TMapper.getOrderInfoById(orderId);

        return orderInfoById;
    }

    @Override
    public boolean paySuccess(String orderId) {

        MoocOrder2017T moocOrder2017T = new MoocOrder2017T();
        moocOrder2017T.setUuid(orderId);
        moocOrder2017T.setOrderStatus(1);

        Integer integer = moocOrder2017TMapper.updateById(moocOrder2017T);
        if (integer >= 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {
        MoocOrder2017T moocOrderT = new MoocOrder2017T();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(2);

        Integer integer = moocOrder2017TMapper.updateById(moocOrderT);
        if (integer >= 1) {
            return true;
        } else {
            return false;
        }
    }
}
