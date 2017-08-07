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
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.messaging.Message;
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

	private final CounterService counterService;
	private FluxSink<Message<Comment>> commentSink;
	private Flux<Message<Comment>> flux;

	public CommentController(CounterService counterService) {
		this.counterService = counterService;
		this.flux = Flux.<Message<Comment>>create(
			emitter -> this.commentSink = emitter,
			FluxSink.OverflowStrategy.IGNORE)
			.publish()
			.autoConnect();

	}

	@PostMapping("/comments")
	public Mono<String> addComment(Comment newComment) {
		if (commentSink != null) {
			return Mono.fromRunnable(() -> commentSink.next(MessageBuilder
				.withPayload(newComment)
				.build()))
				.then(Mono.just("redirect:/"));
		} else {
			return Mono.just("redirect:/");
		}
	}

	@StreamEmitter
	@Output(Source.OUTPUT)
	public void emit(FluxSender output) {
		output.send(this.flux);
	}

}
// end::code[]