package com.stylefeng.guns.rest.modular;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class TestFile {
    public static void main(String[] args) {
        String filePath = "D:\\";
        String fileName = "活动创建表模板11.xlsx";
        // TODO 服务器路径是linux，上线前需要补充
        File file = new File(filePath + fileName);
        OutputStream out = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("出异常了");
            e.printStackTrace();
        }
    }
}

