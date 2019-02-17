package com.jdbctemplate.projections.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jdbctemplate.projections.demo.manager.EntityManager;

@SpringBootApplication
public class JdbcTemplateProjectionDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JdbcTemplateProjectionDemoApplication.class, args);
	}

	@Bean
	public EntityManager entityManager() {
		return new EntityManager("com.jdbctemplate.projections.demo");
	}
}

