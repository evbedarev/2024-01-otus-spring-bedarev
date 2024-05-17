package ru.otus.hw;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		System.out.printf("Чтобы проверить себя открывай: %n%s%n%s%n",
				"curl -vv http://localhost:8080/api/v1/books", "curl -vv http://localhost:8080/edit?id=1");
		SpringApplication.run(Application.class, args);
	}

}
