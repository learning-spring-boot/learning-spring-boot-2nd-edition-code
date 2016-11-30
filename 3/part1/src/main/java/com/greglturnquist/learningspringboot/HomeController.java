/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greglturnquist.learningspringboot;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Greg Turnquist
 */
@RestController
public class HomeController {

	private final ReactiveChapterRepository repository;

	public HomeController(ReactiveChapterRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/")
	public Mono<String> index() {
		return Mono.just("Greetings from a reactive controller!");
	}

	@GetMapping("/chapters")
	public Flux<Chapter> findAll() {
		return repository.findAll();
	}

	@GetMapping("/chapters/{id}")
	public Mono<Chapter> findOne(@PathVariable String id) {
		return repository.findOne(id);
	}

	@PostMapping("/chapters")
	public Flux<Chapter> create(@RequestBody Flux<Chapter> chapterStream) {
		return repository.save(chapterStream);
	}

	@DeleteMapping("/chapters")
	public Mono<Void> clean() {
		return repository.deleteAll();
	}

}
