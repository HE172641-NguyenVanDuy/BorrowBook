package com.borrowbook.duyanh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DuyanhApplication {

	public static void main(String[] args) {
		SpringApplication.run(DuyanhApplication.class, args);
	}

}
