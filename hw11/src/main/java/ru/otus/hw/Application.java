package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		System.out.printf("Для создания тестовых данных открывай: %n%s%n%s%n%n",
				"curl -vv -XPOST http://localhost:8080/api/v1/test_data/1",
				"curl -vv -XPOST http://localhost:8080/api/v1/test_data/2");
		System.out.printf("Для получения списка всех книг открывай: %n%s%n",
				"curl -vv http://localhost:8080/api/v1/books");
		SpringApplication.run(Application.class, args);
	}
}
