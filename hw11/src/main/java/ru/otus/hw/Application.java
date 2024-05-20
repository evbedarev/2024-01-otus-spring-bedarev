package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import ru.otus.hw.repository.BookRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackageClasses = BookRepository.class)
public class Application {

	public static void main(String[] args) {
		System.out.printf("Чтобы проверить себя открывай: %n%s%n%s%n",
				"curl -vv http://localhost:8080/api/v1/books", "curl -vv http://localhost:8080/edit?id=1");
		SpringApplication.run(Application.class, args);
	}
	@Bean
	public MongockInitializingBeanRunner getBuilder(MongoClient reactiveMongoClient, ApplicationContext context) {
		return MongockSpringboot.builder()
				.setDriver(MongoReactiveDriver.withDefaultLock(reactiveMongoClient, "user_db"))
				.addMigrationScanPackage("ru.otus.hw.changelog")
				.setSpringContext(context)
				.setTransactionEnabled(true)
				.buildInitializingBeanRunner();
	}

	@Bean
	MongoClient mongoClient() {
		CodecRegistry codecRegistry = fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		return MongoClients.create(MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString("mongodb://myUserAdmin:Qq123456@localhost:27017/admin?retryWrites=false"))
				.codecRegistry(codecRegistry)
				.build());
	}

}
