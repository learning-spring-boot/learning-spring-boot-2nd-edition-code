package com.greglturnquist.learningspringboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

	@Bean
	CommandLineRunner init(ChapterRepository repository) {
		return args -> {
			repository.save(
				new Chapter("Quick start with Java"));
			repository.save(
				new Chapter("Reactive Web with Spring Boot"));
			repository.save(
				new Chapter("...and more!"));
		};
	}

}
