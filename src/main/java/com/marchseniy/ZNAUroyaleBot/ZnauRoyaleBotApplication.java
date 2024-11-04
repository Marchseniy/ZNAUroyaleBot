package com.marchseniy.ZNAUroyaleBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.marchseniy.ZNAUroyaleBot.database.repositories")
public class ZnauRoyaleBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(ZnauRoyaleBotApplication.class, args);
	}

	@GetMapping("/healthz")
	public String healthCheck() {
		return "OK";
	}
}