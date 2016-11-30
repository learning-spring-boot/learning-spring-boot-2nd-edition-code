package com.greglturnquist.learningspringboot.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

// tag::code[]
@SpringCloudApplication
@EnableZuulProxy
public class LearningSpringBootChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(
			LearningSpringBootChatApplication.class, args);
	}
}
// end::code[]