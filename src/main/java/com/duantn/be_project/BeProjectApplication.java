package com.duantn.be_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Kích hoạt tính năng lập lịch
@EnableAsync
public class BeProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeProjectApplication.class, args);
	}

}
