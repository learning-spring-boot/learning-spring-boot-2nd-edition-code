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

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.core.userdetails.MapUserDetailsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@EnableWebFluxSecurity
public class SecurityConfiguration {

	// tag::mongodb-users[]
//	@Autowired
//	public void globalUserDetails(AuthenticationManagerBuilder auth,
//					  SpringDataUserDetailsService userDetailsService)
//		throws Exception {
//
//		auth.userDetailsService(userDetailsService);
//	}

//	@Bean
//	CommandLineRunner initializeUsers(UserRepository repository) {
//		return args -> {
//			repository.save(new User(null, "greg", "turnquist",
//				new String[]{"ROLE_USER", "ROLE_ADMIN"}));
//
//			repository.save(new User(null, "phil", "webb",
//				new String[]{"ROLE_USER"}));
//		};
//	}

	@Bean
	MapUserDetailsRepository userDetailsRepository(SpringDataUserDetailsService userDetailsService,
												   UserRepository repository) throws Exception {
		repository.deleteAll();
		repository.save(new User(null, "greg", "turnquist",
			new String[]{"ROLE_USER", "ROLE_ADMIN"}));

		repository.save(new User(null, "phil", "webb",
			new String[]{"ROLE_USER"}));

		UserDetails greg = userDetailsService.loadUserByUsername("greg");
		UserDetails phil = userDetailsService.loadUserByUsername("phil");

		return new MapUserDetailsRepository(greg, phil);
	}
	// end::mongodb-users[]

	@Bean
	SecurityWebFilterChain springWebFilterChain(HttpSecurity http) {
		return http
			.authorizeExchange()
				.pathMatchers("/**").authenticated()
				.and()
			.build();
	}
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//			.httpBasic()
//				.and()
//			.formLogin()
//				.and()
//			.authorizeRequests()
//				.antMatchers("/**").authenticated();
//	}

}
// end::code[]
