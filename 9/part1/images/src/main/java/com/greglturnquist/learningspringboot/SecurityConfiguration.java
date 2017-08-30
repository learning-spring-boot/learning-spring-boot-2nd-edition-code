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

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionSecurityContextRepository;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

	/**
	 * First, load authentication from current session.
	 * Then, insert into the ServerWebExchange for downstream consumption.
	 */
//	@Bean
//	@Order(-200)
//	WebFilter
//	loadAuthFromSessionFilter(
//				ReactiveMongoOperationsSessionRepository repository) {
//		return (exchange, chain) -> loadFromSession(exchange, repository)
//			.map(authentication -> exchange.mutate()
//				.principal(Mono.just(authentication))
//				.build())
//			.flatMap(serverWebExchange -> chain.filter(serverWebExchange));
//	}

	@Bean
	SecurityWebFilterChain springWebFilterChain(/*HttpSecurity http,
					ReactiveAuthenticationManager authenticationManager*/) {
		return HttpSecurity.http()
			.securityContextRepository(new WebSessionSecurityContextRepository())
			.authorizeExchange()
				.anyExchange().authenticated()
				.and()
//			.authenticationManager(authenticationManager)
			.build();
	}

//	@Bean
//	AuthenticationWebFilter authWebFilter(HttpSecurity http,
//										  ReactiveAuthenticationManager authenticationManager,
//										  ReactiveMongoOperationsSessionRepository repository) {
//		AuthenticationWebFilter authenticationWebFilter = http
//			.httpBasic()
//			.authenticationManager(authenticationManager)
//			.build();
//
//		// Replace default authentication converter with this one.
//		authenticationWebFilter.setAuthenticationConverter(exchange -> loadFromSession(exchange, repository));
//
//		return authenticationWebFilter;
//	}

	/**
	 * We depend on the upstream service providing user details so
	 * we don't have to connect to the user store here.
	 */
//	@Bean
//	ReactiveAuthenticationManager authenticationManager() {
//		return authentication -> Mono.justOrEmpty(authentication);
//	}

//	private static Mono<Authentication> loadFromSession(
//				ServerWebExchange exchange,
//				ReactiveMongoOperationsSessionRepository repository) {
//		return exchange.getSession()
//			.map(webSession -> webSession.getId())
//			.flatMap(id -> repository.findById(id))
//			.<SecurityContext> map(mongoSession -> mongoSession.getAttribute("USER"))
//			.map(securityContext -> securityContext.getAuthentication());
//	}
}
// end::code[]