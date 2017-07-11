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

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Greg Turnquist
 */
// tag::intro[]
@Configuration
public class WebSocketConfig {
// end::intro[]

	// tag::cloud-1[]
	@Profile("cloud")
	@Configuration
	@EnableConfigurationProperties(ChatConfigProperties.class)
//	@EnableWebSocketMessageBroker
	static class CloudBasedWebSocketConfig /*extends AbstractSecurityWebSocketMessageBrokerConfigurer*/ {
	// end::cloud-1[]

		private static final Logger log = LoggerFactory.getLogger(CloudBasedWebSocketConfig.class);

		// tag::cloud-2[]
		private final ChatConfigProperties chatConfigProperties;

		CloudBasedWebSocketConfig(ChatConfigProperties chatConfigProperties) {
			this.chatConfigProperties = chatConfigProperties;
		}
		// end::cloud-2[]

		// tag::cors[]
//		@Override
//		public void registerStompEndpoints(StompEndpointRegistry registry) {
//			registry.addEndpoint("/learning-spring-boot")
//				.setAllowedOrigins(chatConfigProperties.getOrigin())
//				.withSockJS();
//		}
		// end::cors[]

//		@Override
//		public void configureMessageBroker(MessageBrokerRegistry registry) {
//			registry.setApplicationDestinationPrefixes("/app");
//			registry.enableSimpleBroker("/topic", "/queue");
//		}

//		@Override
//		protected void configureInbound(
//			MessageSecurityMetadataSourceRegistry messages) {
//
//			configureMessageSecurity(messages);
//		}
	}

	// tag::websocket-non-cloud[]
	@Profile("!cloud")
	@Configuration
//	@EnableWebSocketMessageBroker
	static class LocalWebSocketConfig /*extends AbstractSecurityWebSocketMessageBrokerConfigurer*/ {
	// end::websocket-non-cloud[]

		private static final Logger log = LoggerFactory.getLogger(LocalWebSocketConfig.class);

//		@Override
//		public void registerStompEndpoints(StompEndpointRegistry registry) {
//			registry.addEndpoint("/learning-spring-boot").withSockJS();
//		}

//		@Override
//		public void configureMessageBroker(MessageBrokerRegistry registry) {
//			registry.setApplicationDestinationPrefixes("/app");
//			registry.enableSimpleBroker("/topic", "/queue");
//		}

		// tag::non-cloud-configure-inbound[]
//		@Override
//		protected void configureInbound(
//			MessageSecurityMetadataSourceRegistry messages) {
//
//			configureMessageSecurity(messages);
//		}
		// end::non-cloud-configure-inbound[]
	}

	// tag::configure-message-security[]
//	private static void configureMessageSecurity(
//		MessageSecurityMetadataSourceRegistry messages) {
//
//		messages
//			.nullDestMatcher().authenticated()
//			.simpDestMatchers("/app/**").hasRole("USER")
//			.simpSubscribeDestMatchers(
//				"/user/**", "/topic/**").hasRole("USER")
//			.anyMessage().denyAll();
//	}
	// end::configure-message-security[]
}
