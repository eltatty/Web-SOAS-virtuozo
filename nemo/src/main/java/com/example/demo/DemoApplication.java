package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		new File(HomeController.uploadDirectory).mkdir();
		SpringApplication.run(DemoApplication.class, args);
	}

}
