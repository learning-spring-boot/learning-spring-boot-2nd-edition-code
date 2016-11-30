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

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoClientFactoryBean;
import org.springframework.data.mongodb.core.ReactiveMongoClientSettingsFactoryBean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository;
import org.springframework.data.repository.query.DefaultEvaluationContextProvider;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.reactivestreams.client.MongoClient;

/**
 * @author Greg Turnquist
 */
@Configuration
public class ReactiveMongoConfiguration implements BeanClassLoaderAware, BeanFactoryAware {

	@Value("${mongo.database:test}")
	private String database;

	private ClassLoader classLoader;
	private BeanFactory beanFactory;

	@Bean
	ReactiveChapterRepository reactiveChapterRepository(ReactiveMongoRepositoryFactory factory) {
		return factory.getRepository(ReactiveChapterRepository.class);
	}

	@Bean
	ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json().build();
	}

	@Bean
	ReactiveMongoClientSettingsFactoryBean reactiveMongoClientSettingsFactoryBean() {
		ReactiveMongoClientSettingsFactoryBean factoryBean = new ReactiveMongoClientSettingsFactoryBean();
		factoryBean.setClusterSettings(ClusterSettings.builder()
			.hosts(Arrays.asList(new ServerAddress()))
			.build());
		return factoryBean;
	}

	@Bean
	ReactiveMongoClientFactoryBean reactiveMongoClientFactoryBean(MongoClientSettings mongoClientSettings) {
		ReactiveMongoClientFactoryBean factoryBean = new ReactiveMongoClientFactoryBean();
		factoryBean.setMongoClientSettings(mongoClientSettings);
		return factoryBean;
	}

	@Bean
	ReactiveMongoDatabaseFactory reactiveMongoDbFactory(MongoClient mongoClient){
		return new SimpleReactiveMongoDatabaseFactory(mongoClient, database);
	}

	@Bean
	ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory mongoDbFactory){
		return new ReactiveMongoTemplate(mongoDbFactory);
	}

	@Bean
	ReactiveMongoRepositoryFactory reactiveMongoRepositoryFactory(ReactiveMongoOperations reactiveMongoOperations){

		ReactiveMongoRepositoryFactory factory = new ReactiveMongoRepositoryFactory(reactiveMongoOperations);
		factory.setRepositoryBaseClass(SimpleReactiveMongoRepository.class);
		factory.setBeanClassLoader(classLoader);
		factory.setBeanFactory(beanFactory);
		factory.setEvaluationContextProvider(DefaultEvaluationContextProvider.INSTANCE);

		return factory;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
