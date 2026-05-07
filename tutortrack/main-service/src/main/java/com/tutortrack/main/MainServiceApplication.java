package com.tutortrack.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(MainServiceApplication.class, args);
		System.out.printf("Чтобы проверить себя открывай: %n http://localhost:8080 %n");
	}

}
