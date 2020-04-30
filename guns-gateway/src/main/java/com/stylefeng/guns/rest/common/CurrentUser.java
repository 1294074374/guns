package com.stylefeng.guns.rest.common;

public class CurrentUser {
    // 线程绑定的存储空间
    private static final InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void saveUserId(String userId) {
        threadLocal.set(userId);
    }

    public static String getCurrentUser() {
        return threadLocal.get();
    }


//    // 保存用户信息
//    public static void saveUserInfo(UserInfoModel userInfoModel){
//        threadLocal.set(userInfoModel);
//    }
//
//    // 获取用户信息
//    public static UserInfoModel getCurrentUser(){
//        return threadLocal.get();
//    }
}
