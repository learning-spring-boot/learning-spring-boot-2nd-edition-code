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
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Service
@EnableBinding(Sink.class)
public class CommentService {

	private final static Logger log =
		LoggerFactory.getLogger(CommentService.class);

	private final SimpMessagingTemplate simpMessagingTemplate;

	public CommentService(SimpMessagingTemplate
							  simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@StreamListener(Sink.INPUT)
	public void broadcast(Comment comment) {
		log.info("Publishing " + comment.toString() +
			" to websocket...");
		simpMessagingTemplate.convertAndSend(
			"/topic/comments.new", comment);
	}

}
// end::code[]
