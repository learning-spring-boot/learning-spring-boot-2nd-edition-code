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
package com.greglturnquist.learningspringboot.images;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.greglturnquist.learningspringboot.ImagesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * @author Greg Turnquist
 */
@Component
public class CommentHelper {

	private static final Logger log = LoggerFactory.getLogger(CommentHelper.class);

	private final RestTemplate restTemplate;

	private final ImagesConfiguration imagesConfiguration;

	CommentHelper(RestTemplate restTemplate,
				  ImagesConfiguration imagesConfiguration) {
		this.restTemplate = restTemplate;
		this.imagesConfiguration = imagesConfiguration;
	}

	// tag::get-comments[]
	@HystrixCommand(fallbackMethod = "defaultComments")
	public List<Comment> getComments(Image image) {

		ResponseEntity<List<Comment>> results = restTemplate.exchange(
			"http://COMMENTS/comments/{imageId}",
			HttpMethod.GET,
			new HttpEntity<>(new HttpHeaders() {{
				String credentials = imagesConfiguration.getCommentsUser() + ":" +
					imagesConfiguration.getCommentsPassword();
				String token = new String(Base64.encode(credentials.getBytes()));
				set(AUTHORIZATION, "Basic " + token);
			}}),
			new ParameterizedTypeReference<List<Comment>>() {},
			image.getId());
		return results.getBody();
	}
	// end::get-comments[]

	// tag::fallback[]
	public List<Comment> defaultComments(Image image) {
		return Collections.emptyList();
	}
	// end::fallback[]
}
