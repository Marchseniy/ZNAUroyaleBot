package com.marchseniy.ZNAUroyaleBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Collections;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.marchseniy.ZNAUroyaleBot.database.repositories")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ZnauRoyaleBotApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ZnauRoyaleBotApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", System.getenv("PORT")));
		app.run(args);
	}
}