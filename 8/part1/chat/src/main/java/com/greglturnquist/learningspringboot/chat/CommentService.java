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
package com.greglturnquist.learningspringboot.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.core.ResolvableType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service
@EnableBinding(Sink.class)
public class CommentService implements WebSocketHandler {

	private final static Logger log =
		LoggerFactory.getLogger(CommentService.class);

	private Flux<Comment> flux;
	private FluxSink<Comment> webSocketCommentSink;

	private Jackson2JsonEncoder encoder;

	CommentService(Jackson2JsonEncoder encoder) {
		this.encoder = encoder;
		this.flux = Flux.<Comment>create(
			emitter -> this.webSocketCommentSink = emitter,
			FluxSink.OverflowStrategy.IGNORE)
				.publish()
				.autoConnect();
	}

	@StreamListener(Sink.INPUT)
	public void broadcast(Comment comment) {
		log.info("Publishing " + comment.toString() +
			" to websocket...");
		webSocketCommentSink.next(comment);
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {

		return session.send(encoder
			.encode(
				this.flux,
				session.bufferFactory(),
				ResolvableType.forClass(Comment.class),
				null,
				null)
			.map(dataBuffer -> session.binaryMessage(dataBufferFactory -> dataBuffer)));
	}

}
// end::code[]
