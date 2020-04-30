package com.stylefeng.guns;

import com.stylefeng.guns.rest.OrderApplication;
import com.stylefeng.guns.rest.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes  = OrderApplication.class)

public class GunsRestApplicationTests {

	@Test
	public void contextLoads() {
		FTPUtil ftpUtil = new FTPUtil();
		String fileStrByAddress = ftpUtil.getFileStrByAddress("seats/cgs.json");

		System.out.println(fileStrByAddress);
	}

}
