package com.stylefeng.guns.api.base.tips;

import lombok.extern.slf4j.Slf4j;

/**
 * 返回给前台的错误提示
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午5:05:22
 */

@Slf4j
public class ErrorTip extends Tip {

    public ErrorTip(int status, String msg) {
        super();
        // token错误
        if (status == 700) {
            this.status = status;
            this.msg = msg;
        } else {
            log.error("系统出现异常，异常code={}，异常信息为={}", status, msg);
            this.status = 999;
            this.msg = "系统出现异常，请联系管理员";
        }

//        super();
//        this.status = 999;
//        this.msg = "系统出现异常，请联系管理员";
    }
}
