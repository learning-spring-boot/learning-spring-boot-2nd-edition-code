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

import org.apache.http.auth.BasicUserPrincipal;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * @author Greg Turnquist
 */
// tag::websocket-1[]
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
// end::websocket-1[]

	// tag::websocket-2[]
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/learning-spring-boot")
			.setHandshakeHandler(new UserParsingHandshakeHandler())
			.setAllowedOrigins("http://localhost:8080")
			.withSockJS();
	}
	// end::websocket-2[]

	// tag::websocket-3[]
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic", "/queue");
	}
	// end::websocket-3[]

	// tag::websocket-4[]
	static class UserParsingHandshakeHandler
		extends DefaultHandshakeHandler {

		@Override
		protected Principal determineUser(ServerHttpRequest request,
										  WebSocketHandler wsHandler,
										  Map<String, Object> attributes) {
			ServletServerHttpRequest servletRequest =
				(ServletServerHttpRequest) request;
			HttpServletRequest httpServletRequest =
				servletRequest.getServletRequest();
			return new BasicUserPrincipal(
				httpServletRequest.getParameter("user"));
		}
	}
	// end::websocket-4[]

}
