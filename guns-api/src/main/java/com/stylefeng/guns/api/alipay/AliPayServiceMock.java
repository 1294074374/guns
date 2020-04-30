package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;

/*
业务降级方法
 */
public class AliPayServiceMock implements AliPayServiceAPI {
    @Override
    public AlipayInfoVO getQRCode(String orderId) {

        return null;
    }

    @Override
    public AlipayResultVO getOrderStatus(String orderId) {

        AlipayResultVO alipayResultVO = new AlipayResultVO();
        alipayResultVO.setOrderId(orderId);
        alipayResultVO.setOrderStatus(0);
        alipayResultVO.setOrderMsg("尚未支付成功");
        return alipayResultVO;
    }
}
