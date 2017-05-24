package com.greglturnquist.learningspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

// tag::code[]
@SpringBootApplication(exclude = ThymeleafAutoConfiguration.class)
public class LearningSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(
			LearningSpringBootApplication.class, args);
	}

	@Bean
	HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}

}
// end::code[]