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
package com.greglturnquist.learningspringboot.images;

import reactor.core.publisher.Mono;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Controller
@EnableBinding(Source.class)
public class CommentController {

	private final Source source;

	private final CounterService counterService;

	public CommentController(Source source,
							 CounterService counterService) {
		this.source = source;
		this.counterService = counterService;
	}

	@PostMapping("/comments")
	public Mono<String> addComment(Comment newComment) {
		return Mono.fromRunnable(() -> source.output().send(
			MessageBuilder
				.withPayload(newComment)
				.setHeader(MessageHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build())
		)
			.map(aVoid -> {
				counterService.increment("comments.total.produced");
				counterService.increment(
					"comments." + newComment.getImageId() + ".produced");
				return "redirect:/";
			});
	}
}
// end::code[]