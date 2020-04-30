package com.stylefeng.guns.api.user;

import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;

public interface UserAPI {
    //登陆
    int login(String username, String password);

    //注册
    boolean register(UserModel userModel);

    //检查用户名
    boolean checkUsername(String username);

    //查询用户信息
    UserInfoModel getUserInfo(int uuid);

    //修改用户信息
    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);
}
