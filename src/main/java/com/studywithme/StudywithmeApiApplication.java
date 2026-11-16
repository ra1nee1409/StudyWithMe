package com.studywithme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StudywithmeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudywithmeApiApplication.class, args);
	}

}
