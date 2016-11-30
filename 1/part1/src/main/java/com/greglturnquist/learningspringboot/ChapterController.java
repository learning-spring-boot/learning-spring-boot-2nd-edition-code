package com.greglturnquist.learningspringboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChapterController {

	private final ChapterRepository repository;

	public ChapterController(ChapterRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/chapters")
	public Iterable<Chapter> listing() {
		return repository.findAll();
	}
}
