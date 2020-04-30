package com.stylefeng.guns.rest.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.persistence.model.MoocOrder2017T;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2020-04-17
 */
public interface MoocOrder2017TMapper extends BaseMapper<MoocOrder2017T> {
    String getSeatsByFieldId(@Param("fieldId") String fieldId);

    OrderVO getOrderInfoById(@Param("orderId") String orderId);

    List<OrderVO> getOrdersByUserId(@Param("userId") Integer userId, @Param("page") Page<OrderVO> page);

    String getSoldSeatsByFieldId(@Param("fieldId") String fieldId);
}
