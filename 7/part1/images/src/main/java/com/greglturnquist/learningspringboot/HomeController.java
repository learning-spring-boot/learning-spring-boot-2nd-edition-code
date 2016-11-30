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

import com.greglturnquist.learningspringboot.images.Comment;
import com.greglturnquist.learningspringboot.images.Image;
import com.greglturnquist.learningspringboot.images.ImageService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

/**
 * @author Greg Turnquist
 */
@Controller
public class HomeController {

	private final ImageService imageService;

	// tag::injection[]
	private final RestTemplate restTemplate;

	public HomeController(ImageService imageService,
						  RestTemplate restTemplate) {
		this.imageService = imageService;
		this.restTemplate = restTemplate;
	}
	// end::injection[]

	@GetMapping("/")
	public String index(Model model, Pageable pageable) {
		final Page<Image> page = imageService.findPage(pageable);

		model.addAttribute("page",
			page.map(image -> new HashMap<String, Object>(){{
				put("id", image.getId());
				put("name", image.getName());
				// tag::comments[]
				put("comments",
					restTemplate.exchange(
						"http://COMMENTS/comments/{imageId}",
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<Comment>>() {},
						image.getId()).getBody());
				// end::comments[]
			}}));
		if (page.hasPrevious()) {
			model.addAttribute("prev", pageable.previousOrFirst());
		}
		if (page.hasNext()) {
			model.addAttribute("next", pageable.next());
		}
		return "index";
	}

}
