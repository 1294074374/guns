package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AliPayServiceAPI;
import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.api.util.TokenBucket;
import com.stylefeng.guns.api.util.ToolUtil;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/order/")
public class OrderController {
    private static TokenBucket tokenBucket = new TokenBucket();
    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = AliPayServiceAPI.class, check = false)
    private AliPayServiceAPI aliPayServiceAPI;

    @Reference(
            interfaceClass = OrderServiceApi.class,
            check = false,
            group = "order2018"
    )
    private OrderServiceApi orderServiceApi;

    @Reference(
            interfaceClass = OrderServiceApi.class,
            check = false,
            group = "order2017"
    )
    private OrderServiceApi orderServiceApi2017;

    public ResponseVO error(Integer fieldId, String soldSeats, String seatsName) {
        return ResponseVO.serviceFail("抱歉，下单的人太多了，请稍后重试");
    }

    /*
        用户下单购票接口
        fieldId	    场次编号
        soldSeats	购买座位编号
        seatsName	购买座位的名称
     */
    @HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "1"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
            })
    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName) {
        try {
            if (tokenBucket.getToken()) {
                // 验证销售座位是否为真
                boolean isTrueSeats = orderServiceApi.isTrueSeats(fieldId + "", seatsName);

                // 已经销售的座位里，有没有这些座位
                boolean isNotSoldSeats = orderServiceApi.isNotSoldSeats(fieldId + "", seatsName);

                // 将销售信息存入数据库
                // 获取当前登录用户
                if (isTrueSeats && isNotSoldSeats) {
                    String userId = CurrentUser.getCurrentUser();
                    if (userId == null || userId.trim().length() == 0) {
                        return ResponseVO.serviceFail("用户未登录");
                    }
                    OrderVO orderVO = orderServiceApi.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.valueOf(userId));
                    if (orderVO == null) {
                        log.error("购票业务异常");
                        return ResponseVO.serviceFail("购票业务异常");
                    } else {
                        return ResponseVO.success(orderVO);
                    }
                } else {
                    return ResponseVO.serviceFail("票已售或票不存在");
                }
            } else {
                return ResponseVO.serviceFail("购票人数过多，请稍后再试");
            }
        } catch (Exception e) {
            log.error("下单购票异常：" + e);
            return ResponseVO.serviceFail("下单购票异常");
        }
    }

    /*

     */
    @RequestMapping(value = "getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize) {

        //获取当前登陆人的信息
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || userId.trim().length() == 0) {
            return ResponseVO.serviceFail("用户未登录");
        }
        //使用当前登陆人已购买的订单
        Page<OrderVO> page = new Page<>(nowPage, pageSize);
        if (userId != null && userId.trim().length() > 0) {
            Page<OrderVO> result = orderServiceApi.getOrderByUserId(Integer.valueOf(userId), page);
            Page<OrderVO> result2017 = orderServiceApi2017.getOrderByUserId(Integer.valueOf(userId), page);
            Integer totalPage = (int) (result.getPages() + result2017.getPages());
            //将2017和2018的数量合并
            List<OrderVO> orderVOList = new ArrayList<>();
            orderVOList.addAll(result.getRecords());
            orderVOList.addAll(result2017.getRecords());

            return ResponseVO.success(nowPage, (int) result.getPages(), "", orderVOList);
        } else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }

    @RequestMapping(value = "getPayInfo", method = RequestMethod.POST)
    public ResponseVO getPayInfo(@RequestParam("orderId") String orderId) {
        // 获取当前登陆人的信息
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || userId.trim().length() == 0) {
            return ResponseVO.serviceFail("用户未登录");
        }
        AlipayInfoVO alipayInfoVO = aliPayServiceAPI.getQRCode(orderId);
        if (alipayInfoVO == null) {
            return ResponseVO.serviceFail("支付异常");
        } else {

            return ResponseVO.success(IMG_PRE, alipayInfoVO);
        }

    }

    @RequestMapping(value = "getPayResult", method = RequestMethod.POST)
    public ResponseVO getPayResult(
            @RequestParam("orderId") String orderId,
            @RequestParam(name = "tryNums", required = false, defaultValue = "1") Integer tryNums) {
        // 获取当前登陆人的信息
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || userId.trim().length() == 0) {
            return ResponseVO.serviceFail("用户未登录");
        }
        // 判断是否支付超时
        if (tryNums >= 4) {
            return ResponseVO.serviceFail("订单支付失败，请稍后重试");
        } else {
            AlipayResultVO alipayResultVO = aliPayServiceAPI.getOrderStatus(orderId);
            if (alipayResultVO == null || ToolUtil.isEmpty(alipayResultVO.getOrderId())) {
                AlipayResultVO serviceFailVO = new AlipayResultVO();
                serviceFailVO.setOrderId(orderId);
                serviceFailVO.setOrderStatus(0);
                serviceFailVO.setOrderMsg("支付不成功");
                return ResponseVO.success(serviceFailVO);
            }
            return ResponseVO.success(alipayResultVO);
        }
    }

}
