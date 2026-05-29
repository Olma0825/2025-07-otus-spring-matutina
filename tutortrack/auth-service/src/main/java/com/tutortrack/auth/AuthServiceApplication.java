package com.tutortrack.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(AuthServiceApplication.class, args);
		System.out.printf("Чтобы проверить себя открывай: %n http://localhost:8081 %n");
	}

}
