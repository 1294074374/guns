package com.stylefeng.guns;

import com.stylefeng.guns.rest.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.net.www.protocol.ftp.FtpURLConnection;

import java.io.File;

@RunWith(SpringRunner.class)

public class GunsRestApplicationTests {
	@Autowired
	private FTPUtil  ftpUtil;

	@Test
	public void contextLoads() {
		File file  =new File("E:\\qrcode\\qr-124583135asdf81.png");
		boolean qrCode = ftpUtil.uploadFile("qr-124583135asdf81.png", file);
		System.out.println(qrCode);
	}

}
