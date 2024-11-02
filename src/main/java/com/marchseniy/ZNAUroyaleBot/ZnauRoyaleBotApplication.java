package com.marchseniy.ZNAUroyaleBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.marchseniy.ZNAUroyaleBot.database.repositories")
public class ZnauRoyaleBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(ZnauRoyaleBotApplication.class, args);
	}
}