package com.awbd.lab8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class Lab8Application {

	public static void main(String[] args) {
		SpringApplication.run(Lab8Application.class, args);
	}

}
