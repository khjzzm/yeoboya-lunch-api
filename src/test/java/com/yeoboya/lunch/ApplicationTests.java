package com.yeoboya.lunch;

import com.yeoboya.lunch.config.util.ValidationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		if (applicationContext != null) {
			ValidationUtils.printBeanNames(applicationContext);
		}
	}

}
