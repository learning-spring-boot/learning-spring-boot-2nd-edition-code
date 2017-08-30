/*
 * Copyright 2017 the original author or authors.
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

import org.springframework.cloud.gateway.filter.factory.WebFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.tuple.Tuple;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebSession;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Configuration
public class GatewayConfig {

	@Bean
	SaveSessionWebFilterFactory saveSessionWebFilterFactory() {
		return new SaveSessionWebFilterFactory();
	}

	@Bean
	EmbedSessionHeaderWebFilterFactory embedSessionHeaderWebFilterFactory() {
		return new EmbedSessionHeaderWebFilterFactory();
	}

	/**
	 * Force the current WebSession to get saved
	 */
	static class SaveSessionWebFilterFactory implements WebFilterFactory {
		@Override
		public WebFilter apply(Tuple args) {
			return (exchange, chain) -> exchange.getSession()
				.map(webSession -> {
					System.out.println("Session id: " + webSession.getId() + " Attributes: " + webSession.getAttributes());
					webSession.getAttributes().entrySet().forEach(entry -> System.out.println(entry.getKey() + " => " + entry.getValue()));
					return webSession;
				})
				.map(WebSession::save)
				.then(chain.filter(exchange));
		}
	}

	/**
	 * Add a "SESSION" header with the current WebSession id.
	 */
	static class EmbedSessionHeaderWebFilterFactory implements WebFilterFactory {
		@Override
		public WebFilter apply(Tuple args) {
			return (exchange, chain) -> exchange.getSession()
				.map(webSession -> exchange
					.mutate()
					.request(exchange.getRequest()
						.mutate()
						.header("Cookie", "SESSION="+webSession.getId())
						.build())
					.build())
				.flatMap(chain::filter);
		}
	}
}
// end::code[]
