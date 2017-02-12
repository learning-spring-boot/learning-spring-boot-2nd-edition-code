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
package com.greglturnquist.learningspringboot;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.ISpringWebReactiveTemplateEngine;
import org.thymeleaf.spring5.SpringWebReactiveTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;

/**
 * @author Greg Turnquist
 */
// tag::1[]
@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
public class ReactiveThymeleafConfig {

	private ThymeleafProperties thymeleafProperties;

	public ReactiveThymeleafConfig(ThymeleafProperties thymeleafProperties) {
		this.thymeleafProperties = thymeleafProperties;
	}
	// end::1[]

	// tag::2[]
	@Bean
	public SpringResourceTemplateResolver thymeleafTemplateResolver(ApplicationContext applicationContext) {

		final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix(this.thymeleafProperties.getPrefix());
		resolver.setSuffix(this.thymeleafProperties.getSuffix());
		resolver.setTemplateMode(this.thymeleafProperties.getMode());
		if (this.thymeleafProperties.getEncoding() != null) {
			resolver.setCharacterEncoding(this.thymeleafProperties.getEncoding().name());
		}
		resolver.setCacheable(this.thymeleafProperties.isCache());
		final Integer order = this.thymeleafProperties.getTemplateResolverOrder();
		if (order != null) {
			resolver.setOrder(order);
		}
		resolver.setCheckExistence(this.thymeleafProperties.isCheckTemplate());

		return resolver;
	}
	// end::2[]

	// tag::3[]
	@Bean
	public ISpringWebReactiveTemplateEngine thymeleafTemplateEngine(
		SpringResourceTemplateResolver templateResolver){
		final SpringWebReactiveTemplateEngine templateEngine = new SpringWebReactiveTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}
	// end::3[]

	// tag::4[]
	@Bean
	public ThymeleafReactiveViewResolver thymeleafFullModeViewResolver(
		ISpringWebReactiveTemplateEngine templateEngine){
		final ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
		viewResolver.setTemplateEngine(templateEngine);
		viewResolver.setResponseMaxChunkSizeBytes(16384);
		return viewResolver;
	}
	// end::4[]

}
