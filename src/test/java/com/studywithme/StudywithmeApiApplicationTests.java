package com.studywithme;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class StudywithmeApiApplicationTests {

	@Autowired
	private Environment environment;

	@Test
	void contextLoads() {
		assertNotNull(environment.getProperty("spring.datasource.url"));
		if (Files.exists(Path.of(".env"))) {
			assertFalse(environment.getProperty("payos.client-id", "").isBlank(),
					"Spring Boot phải nạp được biến môi trường từ file .env");
		}
	}

}
