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
package com.greglturnquist.learningspringboot.comments;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @author Greg Turnquist
 */
// tag::stream-1[]
@Service
@EnableBinding(Sink.class)
public class CommentService {
	// end::stream-1[]

	private final CommentRepository repository;

	private final CounterService counterService;

	public CommentService(CommentRepository repository,
						  CounterService counterService) {
		this.repository = repository;
		this.counterService = counterService;
	}

	// tag::stream-2[]
	@StreamListener(Sink.INPUT)
	public Mono<Void> save(Flux<Comment> newComment) {
		return repository
			.saveAll(newComment)
			.map(comment -> {
				counterService.increment(
					"comments.total.consumed");
				counterService.increment(
					"comments." + comment.getImageId() + ".consumed");
				return comment;
			})
			.then();
	}
	// end::stream-2[]

	@Bean
	CommandLineRunner setUp(CommentRepository repository) {
		return args -> {
			repository.deleteAll().subscribe();
		};
	}
}
