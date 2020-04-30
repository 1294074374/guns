package com.stylefeng.guns.api.util;

import java.util.UUID;

public class UUIDUitl {
    public static String getUuid(){

        return UUID.randomUUID().toString().replace("-","");
    }
}
