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

import reactor.core.publisher.Hooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.reactor.core.ReactorCoreAutoConfiguration;
import org.springframework.boot.autoconfigure.reactor.core.ReactorCoreProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@SpringCloudApplication
@EnableAutoConfiguration(exclude = ReactorCoreAutoConfiguration.class)
@EnableConfigurationProperties(ReactorCoreProperties.class)
public class LearningSpringBootCommentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(
			LearningSpringBootCommentsApplication.class);
	}

	@Autowired
	protected void initialize(ReactorCoreProperties properties) {
		if (properties.getStacktraceMode().isEnabled()) {
			Hooks.onOperatorDebug();
		}
	}
}
// end::code[]
